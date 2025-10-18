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

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import lombok.SneakyThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TypeServiceIntegrationTest {

    private static final Instant CREATED_AT = Instant.parse("2023-01-01T00:00:00Z");
    private static final Instant MODIFIED_AT = Instant.parse("2023-01-02T00:00:00Z");

    private static String createdTypeId;
    private static String createdTypeName;

    @Autowired
    MockMvc mockMvc;

    /**
     * Verifies the Type entity set is reachable via the OData V4 endpoint.
     */
    @SneakyThrows
    @Test
    @Order(1)
    void typesAreExposedViaOdata() {
        mockMvc.perform(get("/odata/v4/TypeService/Type")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").exists());
    }

    /**
     * Ensures a Type entity can be created via the OData service using JSON
     * payloads.
     */
    @SneakyThrows
    @Test
    @Order(2)
    void typeCanBeCreatedViaOdata() {
        String typeId = UUID.randomUUID().toString();
        String typeName = "Created Type";

        mockMvc.perform(post("/odata/v4/TypeService/Type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildTypeJson(typeId, typeName, "Project Type - " + typeName, MODIFIED_AT))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ID").value(typeId))
                .andExpect(jsonPath("$.name").value(typeName));

        createdTypeId = typeId;
        createdTypeName = typeName;
    }

    /**
     * Confirms the created Type can be retrieved directly after creation.
     */
    @SneakyThrows
    @Test
    @Order(3)
    void typeCreatedCanBeRetrievedViaOdata() {
        if (createdTypeId == null) {
            throw new IllegalStateException("Type creation test must run before retrieval test.");
        }

        mockMvc.perform(get(typeEntityUrl(createdTypeId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(createdTypeId))
                .andExpect(jsonPath("$.name").value(createdTypeName));
    }

    /**
     * Validates a Type entity can be updated via PATCH and persists the
     * modification.
     */
    @SneakyThrows
    @Test
    @Order(4)
    void typeCanBeUpdatedViaOdata() {
        String typeId = UUID.randomUUID().toString();
        String initialName = "Initial Type";
        createType(typeId, initialName);

        String updatedName = "Updated Type";
        String updatedHeader = "Project Type - Updated Type";
        Instant updatedModifiedAt = Instant.parse("2023-01-03T00:00:00Z");

        mockMvc.perform(patch(typeEntityUrl(typeId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildTypeJson(typeId, updatedName, updatedHeader, updatedModifiedAt))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());

        mockMvc.perform(get(typeEntityUrl(typeId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(typeId))
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.header").value(updatedHeader));
    }

    /**
     * Confirms a Type entity can be deleted and cannot be retrieved afterwards.
     */
    @SneakyThrows
    @Test
    @Order(5)
    void typeCanBeDeletedViaOdata() {
        String typeId = UUID.randomUUID().toString();
        createType(typeId, "Disposable Type");

        mockMvc.perform(delete(typeEntityUrl(typeId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(typeEntityUrl(typeId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound());
    }

    /**
     * Rejects Type fetch attempts with invalid credentials.
     */
    @SneakyThrows
    @Test
    @Order(6)
    void typeFetchRequiresValidCredentials() {
        mockMvc.perform(get("/odata/v4/TypeService/Type")
                .with(httpBasic("not-admin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Creates a Type entity and asserts the operation succeeds.
     *
     * @param typeId   the UUID of the type
     * @param typeName the human readable name of the type
     */
    @SneakyThrows
    private void createType(String typeId, String typeName) {
        String header = "Project Type - " + typeName;
        mockMvc.perform(post("/odata/v4/TypeService/Type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildTypeJson(typeId, typeName, header, MODIFIED_AT))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated());
    }

    /**
     * Builds a JSON payload for a Type entity using the generated CDS model.
     *
     * @param typeId     the ID of the Type entity to build
     * @param name       the name of the type
     * @param header     the computed header for the type
     * @param modifiedAt the modification timestamp to set
     * @return a JSON string representing a Type entity
     */
    private String buildTypeJson(String typeId, String name, String header, Instant modifiedAt) {
        Type type = Type.create();
        type.setId(typeId);
        type.setName(name);
        type.setHeader(header);
        type.setCreatedAt(CREATED_AT);
        type.setCreatedBy("integration@test.local");
        type.setModifiedAt(modifiedAt);
        type.setModifiedBy("integration@test.local");
        type.setIsActiveEntity(true);
        return type.toJson();
    }

    /**
     * Builds the canonical OData V4 URL for a draft-enabled Type entity.
     *
     * @param typeId the UUID of the type entity
     * @return canonical entity URL with all key properties
     */
    private String typeEntityUrl(String typeId) {
        return "/odata/v4/TypeService/Type(ID=" + typeId + ",IsActiveEntity=true)";
    }
}
