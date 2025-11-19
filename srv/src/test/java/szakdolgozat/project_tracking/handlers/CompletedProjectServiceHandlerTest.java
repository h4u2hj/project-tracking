package szakdolgozat.project_tracking.handlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.messages.Messages;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectsChangeStatusContext;
import szakdolgozat.project_tracking.manager.ProjectManager;
import szakdolgozat.project_tracking.utilities.ProjectNotFoundException;

/**
 * Unit tests for {@link CompletedProjectServiceHandler} ensuring handler logic is
 * delegated correctly.
 */
@ExtendWith(MockitoExtension.class)
class CompletedProjectServiceHandlerTest {

    @Mock
    private ProjectManager projectManager;

    @Mock
    private Messages messages;

    private CompletedProjectServiceHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CompletedProjectServiceHandler(projectManager);
        handler.messages = messages;
    }

    /**
     * Ensures the handler sets the action result when the manager succeeds.
     */
    @Test
    void onChangeStatus_shouldSetResult_whenProjectFound() throws Exception {
        ProjectsChangeStatusContext context = mock(ProjectsChangeStatusContext.class);
        Projects updated = Projects.create();
        when(projectManager.changeStatus(context)).thenReturn(updated);

        handler.onChangeStatus(context);

        verify(projectManager).changeStatus(context);
        verify(context).setResult(updated);
        verify(projectManager, never()).projectByContext(any());
        verify(messages, never()).error(anyString());
    }

    /**
     * Verifies fallback behavior when the project is not found by the manager.
     */
    @Test
    void onChangeStatus_shouldHandleProjectNotFoundException() throws Exception {
        ProjectsChangeStatusContext context = mock(ProjectsChangeStatusContext.class);
        Projects original = Projects.create();
        ProjectNotFoundException exception = new ProjectNotFoundException("Project not found");

        when(projectManager.changeStatus(context)).thenThrow(exception);
        when(projectManager.projectByContext(context)).thenReturn(original);

        handler.onChangeStatus(context);

        verify(projectManager).changeStatus(context);
        verify(projectManager).projectByContext(context);
        verify(context).setResult(original);
        verify(messages).error("Project not found");
    }

    /**
     * Checks that after-read processing locks status fields and sets a result.
     */
    @Test
    void afterRead_shouldMarkStatusReadOnly_andSetResult() {
        CdsReadEventContext context = mock(CdsReadEventContext.class);
        Projects p1 = Projects.create();
        Projects p2 = Projects.create();
        List<Projects> projects = Arrays.asList(p1, p2);

        handler.afterRead(context, projects);

        verify(projectManager).setStatusFieldToReadOnly(projects);
        verify(context).setResult(any());
    }
}
