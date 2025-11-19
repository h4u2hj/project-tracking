package szakdolgozat.project_tracking.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectsChangeStatusContext;
import szakdolgozat.project_tracking.repository.ProjectRepository;
import szakdolgozat.project_tracking.repository.SnapshotRepository;
import szakdolgozat.project_tracking.repository.StatusRepository;

/**
 * Unit tests for {@link ProjectManager} covering lightweight unit testing.
 */
@ExtendWith(MockitoExtension.class)
class ProjectManagerTest {

    @Mock
    private SnapshotRepository snapshotRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private ProjectsChangeStatusContext context;

    private ProjectManager projectManager;

    @BeforeEach
    void setUp() {
        projectManager = new ProjectManager(snapshotRepository, projectRepository, statusRepository);
    }

    /**
     * Ensures same-status change requests return early without updates.
     */
    @Test
    void changeStatus_returnsWithoutUpdatesWhenStatusUnchanged() throws Exception {
        Projects project = Projects.create();
        project.setId("project-id");
        project.setStatusId("status-id");

        ProjectManager spyManager = spy(projectManager);
        doReturn(project).when(spyManager).projectByContext(context);
        when(context.getNewStatus()).thenReturn("status-id");

        Projects result = spyManager.changeStatus(context);

        assertSame(project, result);
        verify(projectRepository, never()).updateProjectStatus(anyString(), anyString(), any());
        verify(snapshotRepository, never()).createSnapshot(anyString(), anyString());
        verify(statusRepository, never()).getFinalStatusByStatusId(anyString());
    }

    /**
     * Marks status field availability read-only for active entities.
     */
    @Test
    void setStatusFieldToReadOnly_updatesAvailabilityForActiveProjects() {
        Projects active = Projects.create();
        active.setHasActiveEntity(true);
        active.setStatusFieldAvailability(7);

        Projects inactive = Projects.create();
        inactive.setHasActiveEntity(false);

        projectManager.setStatusFieldToReadOnly(List.of(active, inactive));

        assertEquals(1, active.getStatusFieldAvailability());
        assertNull(inactive.getStatusFieldAvailability());
    }

    /**
     * Delegates project lookups to the repository.
     */
    @Test
    void projectById_delegatesToRepository() {
        Projects project = Projects.create();
        when(projectRepository.projectById("project-id")).thenReturn(project);

        Projects result = projectManager.projectById("project-id");

        assertSame(project, result);
        verify(projectRepository).projectById("project-id");
    }

    /**
     * Delegates status lookups to the repository.
     */
    @Test
    void getProjectStatusId_delegatesToRepository() {
        when(projectRepository.statusIdByProjectId("project-id")).thenReturn("status-id");

        String result = projectManager.getProjectStatusId("project-id");

        assertEquals("status-id", result);
        verify(projectRepository).statusIdByProjectId("project-id");
    }
}
