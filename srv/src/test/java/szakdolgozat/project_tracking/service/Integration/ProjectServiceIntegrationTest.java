package szakdolgozat.project_tracking.service.Integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
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

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import lombok.SneakyThrows;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectServiceIntegrationTest {

    private static final Instant CREATED_AT = Instant.parse("2025-12-31T23:00:00Z");
    private static final Instant MODIFIED_AT = Instant.parse("2025-12-01T23:00:00Z");
    private static final Instant LAST_STATUS_CHANGE_AT = Instant.parse("2025-10-19T23:00:00Z");
    private static final Instant STATUS_CHANGE_DATE = Instant.parse("2025-11-19T23:00:00Z");
    private static final Instant SECOND_STATUS_CHANGE_DATE = Instant.parse("2025-11-20T23:00:00Z");
    private static final String DEFAULT_STATUS_ID = "65891038-ebb5-4ac4-a16f-22967f58a6ba";
    private static final String UPDATED_STATUS_ID = "6589104e-0e6e-4a1d-bfbb-e497f71eaefb";
    private static final String DEFAULT_TYPE_ID = "982297e8-bcab-45b7-bf22-50ed1225853b";
    private static final String DEFAULT_MANAGER_ID = "17718523-1d9a-4885-a670-21055ef5f57c";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static String createdProjectId;
    private static String createdProjectName;

    @Autowired
    MockMvc mockMvc;

    /**
     * Ensures the project collection can be retrieved through the OData endpoint.
     */
    @Test
    @Order(1)
    @SneakyThrows
    void projectsAreExposedViaOdata() {
        mockMvc.perform(get("/odata/v4/ProjectService/Projects")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").exists());
    }

    /**
     * Creates a new project via POST and verifies the returned payload.
     */
    @Test
    @Order(2)
    @SneakyThrows
    void projectCanBeCreatedViaOdata() {
        String projectId = UUID.randomUUID().toString();
        String projectName = "Project-" + projectId.substring(0, 8);

        mockMvc.perform(post("/odata/v4/ProjectService/Projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectCreationJson(projectId, projectName))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ID").value(projectId))
                .andExpect(jsonPath("$.name").value(projectName))
                .andExpect(jsonPath("$.status_ID").value(DEFAULT_STATUS_ID));

        createdProjectId = projectId;
        createdProjectName = projectName;
    }

    /**
     * Confirms the previously created project is accessible via the entity
     * endpoint.
     */
    @Test
    @Order(3)
    @SneakyThrows
    void projectStatusCanBeChangedViaAction() {
        if (createdProjectId == null) {
            throw new IllegalStateException("Project creation test must run before change status test.");
        }

        mockMvc.perform(post(projectChangeStatusUrl(createdProjectId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildChangeStatusJson(UPDATED_STATUS_ID, STATUS_CHANGE_DATE))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());

        Map<String, Object> project = fetchProject(createdProjectId);
        assertEquals(UPDATED_STATUS_ID, project.get("status_ID"));
        assertEquals(STATUS_CHANGE_DATE, Instant.parse(project.get("lastStatusChangeAt").toString()));
    }

    /**
     * Ensures that providing the same status id through the action does not alter
     * the modifiedBy field.
     */
    @Test
    @Order(4)
    @SneakyThrows
    void projectStatusActionWithSameStatusKeepsModifiedBy() {
        if (createdProjectId == null) {
            throw new IllegalStateException("Project creation test must run before repeated change status test.");
        }

        Map<String, Object> projectBeforeUpdate = fetchProject(createdProjectId);
        String modifiedByBefore = projectBeforeUpdate.get("modifiedBy").toString();
        Instant lastStatusChangeBefore = Instant.parse(projectBeforeUpdate.get("lastStatusChangeAt").toString());

        mockMvc.perform(post(projectChangeStatusUrl(createdProjectId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildChangeStatusJson(UPDATED_STATUS_ID, SECOND_STATUS_CHANGE_DATE))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());

        Map<String, Object> projectAfterUpdate = fetchProject(createdProjectId);

        assertEquals(modifiedByBefore, projectAfterUpdate.get("modifiedBy"),
                "ModifiedBy must remain unchanged when the same status is submitted.");
        assertEquals(lastStatusChangeBefore, Instant.parse(projectAfterUpdate.get("lastStatusChangeAt").toString()),
                "Last status change timestamp should remain unchanged for identical status submissions.");
        assertEquals(UPDATED_STATUS_ID, projectAfterUpdate.get("status_ID"),
                "Status ID should remain the already-set value when the same status is provided.");
    }

    /**
     * Confirms the previously created project is accessible via the entity
     * endpoint.
     */
    @Test
    @Order(5)
    @SneakyThrows
    void projectCreatedCanBeRetrievedViaOdata() {
        if (createdProjectId == null) {
            throw new IllegalStateException("Project creation test must run before retrieval test.");
        }

        mockMvc.perform(get(projectEntityUrl(createdProjectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(createdProjectId))
                .andExpect(jsonPath("$.name").value(createdProjectName));
    }

    /**
     * Validates an existing project can be updated via PATCH.
     */
    @Test
    @Order(6)
    @SneakyThrows
    void projectCanBeUpdatedViaOdata() {
        String projectId = UUID.randomUUID().toString();
        createProject(projectId, "Initial Project");

        String updatedLink = "link-" + projectId.substring(0, 6);
        Instant updatedLastChange = Instant.parse("2025-09-27T23:00:00Z");
        Instant updatedCompletedAt = Instant.parse("2025-09-27T23:00:00Z");

        mockMvc.perform(patch(projectEntityUrl(projectId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectUpdateJson(projectId, updatedLink, UPDATED_STATUS_ID, updatedLastChange,
                        updatedCompletedAt))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());

        mockMvc.perform(get(projectEntityUrl(projectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(projectId))
                .andExpect(jsonPath("$.link").value(updatedLink))
                .andExpect(jsonPath("$.status_ID").value(UPDATED_STATUS_ID))
                .andExpect(jsonPath("$.completedAt").value(updatedCompletedAt.toString()));
    }

    /**
     * Verifies delete attempts are rejected by the Project service authorization.
     */
    @Test
    @Order(7)
    @SneakyThrows
    void projectDeleteIsRejectedForProjectService() {
        String projectId = UUID.randomUUID().toString();
        createProject(projectId, "Disposable Project");

        mockMvc.perform(delete(projectEntityUrl(projectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("403001"));

        mockMvc.perform(get(projectEntityUrl(projectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(projectId));
    }

    /**
     * Ensures invalid credentials cannot access the Project service.
     */
    @Test
    @Order(8)
    @SneakyThrows
    void projectFetchRequiresValidCredentials() {
        mockMvc.perform(get("/odata/v4/ProjectService/Projects")
                .with(httpBasic("not-admin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Helper that provisions a project using the Project service.
     */
    @SneakyThrows
    private void createProject(String projectId, String name) {
        mockMvc.perform(post("/odata/v4/ProjectService/Projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectCreationJson(projectId, name))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated());
    }

    /**
     * Builds the JSON body for project creation requests.
     */
    private String buildProjectCreationJson(String projectId, String name) {
        Projects project = Projects.create();
        project.setId(projectId);
        project.setName(name);
        project.setDescription("description-" + projectId.substring(0, 6));
        project.setLink("link-" + projectId.substring(0, 6));
        project.setStartDate(LocalDate.parse("2009-03-03"));
        project.setStatusFieldAvailability(7);
        project.setStatusId(DEFAULT_STATUS_ID);
        project.setTypeId(DEFAULT_TYPE_ID);
        project.setManagerId(DEFAULT_MANAGER_ID);
        project.setStatus(Map.of("ID", DEFAULT_STATUS_ID));
        project.setType(Map.of("ID", DEFAULT_TYPE_ID));
        project.setManager(Map.of("ID", DEFAULT_MANAGER_ID));
        project.setLastStatusChangeAt(LAST_STATUS_CHANGE_AT);
        project.setCreatedAt(CREATED_AT);
        project.setCreatedBy("createdBy.integration@test.local");
        project.setModifiedAt(MODIFIED_AT);
        project.setModifiedBy("modifiedBy.integration@test.local");
        project.setIsActiveEntity(true);
        return project.toJson();
    }

    /**
     * Builds the JSON payload for the change status action.
     */
    private String buildChangeStatusJson(String newStatusId, Instant changeDate) {
        return "{\"newStatus\":\"" + newStatusId + "\",\"changeDate\":\"" + changeDate.toString() + "\"}";
    }

    /**
     * Helper that composes the change status action URL.
     */
    private String projectChangeStatusUrl(String projectId) {
        return projectEntityUrl(projectId) + "/szakdolgozat.srv.service.ProjectService.changeStatus";
    }

    /**
     * Fetches the project and returns it as a key-value map.
     */
    @SneakyThrows
    private Map<String, Object> fetchProject(String projectId) {
        String content = mockMvc.perform(get(projectEntityUrl(projectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return OBJECT_MAPPER.readValue(content, Map.class);
    }

    /**
     * Builds the JSON payload for project update requests.
     */
    private String buildProjectUpdateJson(String projectId,
            String link,
            String statusId,
            Instant lastStatusChangeAt,
            Instant completedAt) {
        Projects project = Projects.create();
        project.setId(projectId);
        project.setLink(link);
        project.setStatus(Map.of("ID", statusId));
        project.setStatusId(statusId);
        project.setLastStatusChangeAt(lastStatusChangeAt);
        project.setCompletedAt(completedAt);
        project.setIsActiveEntity(true);
        return project.toJson();
    }

    /**
     * Composes the canonical entity URL for the Project service.
     */
    private String projectEntityUrl(String projectId) {
        return "/odata/v4/ProjectService/Projects(ID=" + projectId + ",IsActiveEntity=true)";
    }
}
