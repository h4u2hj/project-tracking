package szakdolgozat.project_tracking.service.Integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import lombok.SneakyThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatusServiceIntegrationTest {

    private static final Instant CREATED_AT = Instant.parse("2023-01-01T00:00:00Z");
    private static final Instant MODIFIED_AT = Instant.parse("2023-01-02T00:00:00Z");

    private static String createdStatusId;
    private static String createdStatusName;

    @Autowired
    MockMvc mockMvc;

    /**
     * Verifies the Status entity set is reachable via the OData V4 endpoint.
     */
    @SneakyThrows
    @Test
    @Order(1)
    void statusAreExposedViaOdata() {
        mockMvc.perform(get("/odata/v4/StatusService/Status") // call the OData endpoint
                .with(httpBasic("admin", "admin"))) // use mock credentials
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.value").exists()); // CAP wraps results in {"value":[ ... ]}
    }

    /**
     * Ensures a Status entity can be created via the OData service using JSON
     * generated from the CDS model.
     */
    @SneakyThrows
    @Test
    @Order(2)
    void statusCanBeCreatedViaOdata() {
        String statusId = UUID.randomUUID().toString();
        String statusName = "Created Status";

        mockMvc.perform(post("/odata/v4/StatusService/Status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildStatusJson(statusId, statusName, true))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ID").value(statusId))
                .andExpect(jsonPath("$.name").value(statusName));

        createdStatusId = statusId;
        createdStatusName = statusName;
    }

    /**
     * Confirms the created Status can be fetched directly via GET.
     */
    @SneakyThrows
    @Test
    @Order(3)
    void statusCreatedCanBeRetrievedViaOdata() {
        if (createdStatusId == null) {
            throw new IllegalStateException("Status creation test must run before retrieval test.");
        }

        mockMvc.perform(get(statusEntityUrl(createdStatusId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(createdStatusId))
                .andExpect(jsonPath("$.name").value(createdStatusName));
    }

    /**
     * Validates a Status entity can be updated via PATCH and the modification is
     * persisted in the persistence layer.
     */
    @SneakyThrows
    @Test
    @Order(4)
    void statusCanBeUpdatedViaOdata() {
        String statusId = UUID.randomUUID().toString();
        createStatus(statusId, "Initial Status", false);

        String updatedName = "Updated Status";

        mockMvc.perform(patch(statusEntityUrl(statusId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildStatusJson(statusId, updatedName, true))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());

        mockMvc.perform(get(statusEntityUrl(statusId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(statusId))
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.isFinalStatus").value(true));
    }

    /**
     * Confirms a Status entity can be deleted and is no longer retrievable
     * afterwards.
     */
    @SneakyThrows
    @Test
    @Order(5)
    void statusCanBeDeletedViaOdata() {
        String statusId = UUID.randomUUID().toString();
        createStatus(statusId, "Disposable Status", false);

        mockMvc.perform(delete(statusEntityUrl(statusId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(statusEntityUrl(statusId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound());
    }

    /**
     * Rejects Status fetch attempts with invalid credentials.
     */
    @SneakyThrows
    @Test
    @Order(6)
    void statusFetchRequiresValidCredentials() {
        mockMvc.perform(get("/odata/v4/StatusService/Status")
                .with(httpBasic("not-admin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Sends a POST request to create a new Status entity and expects a successful
     * response.
     *
     */
    @SneakyThrows
    private void createStatus(String statusId, String name, boolean finalStatus) {
        mockMvc.perform(post("/odata/v4/StatusService/Status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildStatusJson(statusId, name, finalStatus))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated());
    }

    /**
     *
     * Builds a JSON payload for a Status entity.
     *
     * @param statusId    the ID of the Status entity to build
     * @param name        the name of the Status entity to build
     * @param finalStatus whether the Status entity should be marked as final or not
     * @return a JSON string representing a Status entity
     */
    private String buildStatusJson(String statusId, String name, boolean finalStatus) {
        Status status = Status.create();
        status.setId(statusId);
        status.setName(name);
        status.setIsFinalStatus(finalStatus);
        status.setCreatedAt(CREATED_AT);
        status.setCreatedBy("integration@test.local");
        status.setModifiedAt(MODIFIED_AT);
        status.setModifiedBy("integration@test.local");
        status.setIsActiveEntity(true);
        return status.toJson();
    }

    /**
     * Builds the canonical OData V4 URL for a draft-enabled Status entity.
     *
     * @param statusId the UUID of the status entity
     * @return canonical entity URL with all key properties
     */
    private String statusEntityUrl(String statusId) {
        return "/odata/v4/StatusService/Status(ID=" + statusId + ",IsActiveEntity=true)";
    }
}
