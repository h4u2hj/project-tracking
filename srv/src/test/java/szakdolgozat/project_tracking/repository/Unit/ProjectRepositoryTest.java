package szakdolgozat.project_tracking.repository.Unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import szakdolgozat.project_tracking.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProjectRepositoryTest {

    @Mock
    private PersistenceService db;

    @InjectMocks
    private ProjectRepository projectRepository;

    /**
     * Verifies the repository issues update statements for both the status and the
     * last status change timestamp.
     */
    @Test
    void testUpdateProjectStatus_IssuesTwoDistinctUpdates() {
        String projectId = "project-123";
        String newStatusId = "status-987";
        Timestamp changeTimestamp = Timestamp.from(Instant.parse("2025-10-20T10:15:30Z"));

        ArgumentCaptor<CqnUpdate> updateCaptor = ArgumentCaptor.forClass(CqnUpdate.class);

        projectRepository.updateProjectStatus(projectId, newStatusId, changeTimestamp);

        verify(db, times(2)).run(updateCaptor.capture());
        List<CqnUpdate> updates = updateCaptor.getAllValues();

        assertEquals(2, updates.size(), "Two updates should be executed for status and timestamp.");

        CqnUpdate statusUpdate = updates.get(0);
        assertTrue(statusUpdate.ref().toString().contains("Projects"),
                "Status update should target the Projects entity.");
        assertTrue(statusUpdate.toString().contains("status_ID"), "Status update should include the status field.");

        CqnUpdate timestampUpdate = updates.get(1);
        assertTrue(timestampUpdate.ref().toString().contains("Projects"),
                "Timestamp update should target the Projects entity.");
        assertTrue(timestampUpdate.toString().contains("lastStatusChangeAt"),
                "Timestamp update should include the lastStatusChangeAt field.");
    }

    /**
     * Ensures the repository delegates completed time updates to the persistence
     * service.
     */
    @Test
    void testSetCompletedTime_DelegatesUpdateToDb() {
        String projectId = "project-complete";
        Timestamp completionTimestamp = Timestamp.from(Instant.parse("2025-12-31T23:59:59Z"));

        ArgumentCaptor<CqnUpdate> updateCaptor = ArgumentCaptor.forClass(CqnUpdate.class);

        projectRepository.setCompletedTime(projectId, completionTimestamp);

        verify(db).run(updateCaptor.capture());
        CqnUpdate capturedUpdate = updateCaptor.getValue();

        assertNotNull(capturedUpdate, "An update statement should be generated.");
        assertTrue(capturedUpdate.ref().toString().contains("Projects"),
                "The update should target the Projects entity.");
        assertTrue(capturedUpdate.toString().contains("completedAt"),
                "The update should include the completedAt field.");
    }

    /**
     * Confirms selecting the status identifier builds the expected select
     * statement.
     */
    @Test
    void testStatusIdByProjectId_BuildsProjectsSelect() {
        Result mockResult = mock(Result.class);
        when(mockResult.single(Map.class)).thenReturn(Map.of("status_ID", "status-123"));
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        projectRepository.statusIdByProjectId("project-select-check");

        ArgumentCaptor<CqnSelect> selectCaptor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(selectCaptor.capture());

        CqnSelect select = selectCaptor.getValue();
        assertNotNull(select, "A select statement should be generated.");
        assertTrue(select.ref().toString().contains("Projects"), "The select should target the Projects entity.");
        verify(mockResult, times(1)).single(Map.class);
    }

    /**
     * Ensures the status identifier returned by the persistence layer is propagated
     * back to the caller.
     */
    @Test
    void testStatusIdByProjectId_ReturnsResultFromDb() {
        String expectedStatusId = "status-result";
        Result mockResult = mock(Result.class);
        when(mockResult.single(Map.class)).thenReturn(Map.of("status_ID", expectedStatusId));
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        String actualStatusId = projectRepository.statusIdByProjectId("project-fetch-status");

        assertEquals(expectedStatusId, actualStatusId);
        verify(db, times(1)).run(any(CqnSelect.class));
    }

    /**
     * Validates fetching a project delegates to the persistence service with the
     * correct select statement.
     */
    @Test
    void testProjectById_CallsDbWithProjectsSelect() {
        Result mockResult = mock(Result.class);
        Projects project = Projects.create();
        project.setId("project-entity");
        when(mockResult.single(Projects.class)).thenReturn(project);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        projectRepository.projectById("project-entity");

        ArgumentCaptor<CqnSelect> selectCaptor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(selectCaptor.capture());

        CqnSelect select = selectCaptor.getValue();
        assertNotNull(select, "A select statement should be generated.");
        assertTrue(select.ref().toString().contains("Projects"), "The select should target the Projects entity.");
    }

    /**
     * Confirms the repository returns the project entity provided by the
     * persistence layer.
     */
    @Test
    void testProjectById_ReturnsProjectFromDb() {
        String projectId = "project-return";
        Projects expectedProject = Projects.create();
        expectedProject.setId(projectId);

        Result mockResult = mock(Result.class);
        when(mockResult.single(Projects.class)).thenReturn(expectedProject);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Projects actualProject = projectRepository.projectById(projectId);

        assertSame(expectedProject, actualProject);
        verify(db, times(1)).run(any(CqnSelect.class));
        verify(mockResult, times(1)).single(Projects.class);
    }
}
