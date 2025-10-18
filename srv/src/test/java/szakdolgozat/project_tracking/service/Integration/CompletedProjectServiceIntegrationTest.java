package szakdolgozat.project_tracking.service.Integration;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CompletedProjectServiceIntegrationTest {

    private static final Instant CREATED_AT = Instant.parse("2025-12-31T23:00:00Z");
    private static final Instant MODIFIED_AT = Instant.parse("2025-12-01T23:00:00Z");
    private static final Instant LAST_STATUS_CHANGE_AT = Instant.parse("2025-10-19T23:00:00Z");
    private static final Instant COMPLETED_AT = Instant.parse("2025-09-27T23:00:00Z");
    private static final String FINAL_STATUS_ID = "6589104e-0e6e-4a1d-bfbb-e497f71eaefb";
    private static final String DEFAULT_TYPE_ID = "982297e8-bcab-45b7-bf22-50ed1225853b";
    private static final String DEFAULT_MANAGER_ID = "17718523-1d9a-4885-a670-21055ef5f57c";

    @Autowired
    MockMvc mockMvc;

    /**
     * Verifies the completed projects list endpoint responds successfully.
     */
    @Test
    @SneakyThrows
    void completedProjectsAreExposedViaOdata() {
        mockMvc.perform(get("/odata/v4/CompletedProjectService/Projects")
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").exists());
    }

    /**
     * Ensures a newly created completed project can be fetched via the service.
     */
    @Test
    @SneakyThrows
    void completedProjectIsAccessibleViaCompletedService() {
        String projectId = UUID.randomUUID().toString();
        String projectName = "Completed-" + projectId.substring(0, 8);
        createFinalProjectSuccess(projectId, projectName);

        mockMvc.perform(get(completedProjectEntityUrl(projectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID").value(projectId))
                .andExpect(jsonPath("$.name").value(projectName))
                .andExpect(jsonPath("$.status_ID").value(FINAL_STATUS_ID))
                .andExpect(jsonPath("$.completedAt").value(COMPLETED_AT.toString()));
    }

    /**
     * Confirms completed projects can be removed via the OData endpoint.
     */
    @Test
    @SneakyThrows
    void completedProjectCanBeDeletedViaOdata() {
        String projectId = UUID.randomUUID().toString();
        createFinalProjectSuccess(projectId, "Disposable-Completed");

        mockMvc.perform(delete(completedProjectEntityUrl(projectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(completedProjectEntityUrl(projectId))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound());
    }

    /**
     * Checks that invalid credentials are rejected when listing completed projects.
     */
    @Test
    @SneakyThrows
    void completedProjectFetchRequiresValidCredentials() {
        mockMvc.perform(get("/odata/v4/CompletedProjectService/Projects")
                .with(httpBasic("not-admin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Helper that provisions a finished project via the Project service.
     */
    @SneakyThrows
    private void createFinalProjectSuccess(String projectId, String name) {
        mockMvc.perform(post("/odata/v4/ProjectService/Projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildFinalProjectCreationJson(projectId, name))
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated());
    }

    /**
     * Builds the JSON body required to create a completed project.
     */
    private String buildFinalProjectCreationJson(String projectId, String name) {
        Projects project = Projects.create();
        project.setId(projectId);
        project.setName(name);
        project.setDescription("description-" + projectId.substring(0, 6));
        project.setLink("link-" + projectId.substring(0, 6));
        project.setStartDate(LocalDate.parse("2009-03-03"));
        project.setStatusFieldAvailability(7);
        project.setStatusId(FINAL_STATUS_ID);
        project.setTypeId(DEFAULT_TYPE_ID);
        project.setManagerId(DEFAULT_MANAGER_ID);
        project.setStatus(Map.of("ID", FINAL_STATUS_ID));
        project.setType(Map.of("ID", DEFAULT_TYPE_ID));
        project.setManager(Map.of("ID", DEFAULT_MANAGER_ID));
        project.setLastStatusChangeAt(LAST_STATUS_CHANGE_AT);
        project.setCompletedAt(COMPLETED_AT);
        project.setCreatedAt(CREATED_AT);
        project.setCreatedBy("createdBy.integration@test.local");
        project.setModifiedAt(MODIFIED_AT);
        project.setModifiedBy("modifiedBy.integration@test.local");
        project.setIsActiveEntity(true);
        return project.toJson();
    }

    /**
     * Creates the canonical entity URL for CompletedProjectService projects.
     */
    private String completedProjectEntityUrl(String projectId) {
        return "/odata/v4/CompletedProjectService/Projects(ID=" + projectId + ")";
    }
}
