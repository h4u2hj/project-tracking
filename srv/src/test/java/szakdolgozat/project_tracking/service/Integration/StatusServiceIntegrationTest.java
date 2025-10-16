package szakdolgozat.project_tracking.service.Integration;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StatusServiceIntegrationTest {

    private static final Instant CREATED_AT = Instant.parse("2023-01-01T00:00:00Z");
    private static final Instant MODIFIED_AT = Instant.parse("2023-01-02T00:00:00Z");
    @Autowired
    MockMvc mockMvc;

    /**
     * Verifies the Status entity set is reachable via the OData V4 endpoint.
     */
    @SneakyThrows
    @Test
    void types_are_exposed_via_odata() {
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
    void status_can_be_created_via_odata() {
        String statusId = UUID.randomUUID().toString();
        String statusName = "Created Status";

        mockMvc.perform(post("/odata/v4/StatusService/Status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildStatusJson(statusId, statusName, true))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.ID").value(statusId))
                .andExpect(jsonPath("$.name").value(statusName));
    }

    /**
     * Validates a Status entity can be updated via PATCH and the modification is
     * persisted in the persistence layer.
     */
    @SneakyThrows
    @Test
    void status_can_be_updated_via_odata() {
        String statusId = UUID.randomUUID().toString();
        createStatusSuccess(statusId, "Initial Status", false);

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
    void status_can_be_deleted_via_odata() {
        String statusId = UUID.randomUUID().toString();
        createStatusSuccess(statusId, "Disposable Status", false);

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
    void status_fetch_requires_valid_credentials() {
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
    private void createStatusSuccess(String statusId, String name, boolean finalStatus) {
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
