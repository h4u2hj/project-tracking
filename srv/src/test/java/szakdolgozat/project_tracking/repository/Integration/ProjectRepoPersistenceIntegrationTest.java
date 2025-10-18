package szakdolgozat.project_tracking.repository.Integration;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import szakdolgozat.project_tracking.repository.ProjectRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ProjectRepoPersistenceIntegrationTest {

    private static final String PROJECT_ID = "3506561e-ad35-4d0e-905f-7fe5f85bdda1";
    private static final String EXPECTED_STATUS_ID = "65891071-3d68-42ff-981b-988f6a2bc33b";

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Ensures the repository returns the persisted status identifier for the selected project.
     */
    @Test
    void testStatusIdByProjectIdReturnsPersistedValue() {
        String statusId = projectRepository.statusIdByProjectId(PROJECT_ID);

        assertNotNull(statusId, "The status identifier should be resolved for an existing project.");
        assertEquals(EXPECTED_STATUS_ID, statusId, "The repository should return the persisted status identifier.");
    }

    /**
     * Confirms the repository fetches the persisted project entity for the provided identifier.
     */
    @Test
    void testProjectByIdReturnsPersistedProject() {
        Projects project = projectRepository.projectById(PROJECT_ID);

        assertNotNull(project, "The project entity should be resolved from the persistence layer.");
        assertEquals(PROJECT_ID, project.getId(), "The project identifier should match the persisted entry.");
        assertEquals("New Website", project.getName(), "The project name should match the persisted entry.");
        assertEquals(EXPECTED_STATUS_ID, project.getStatusId(), "The project should be associated with the expected status.");
        assertEquals(LocalDate.parse("2020-10-22"), project.getStartDate(), "The project start date should match the persisted entry.");
    }
}
