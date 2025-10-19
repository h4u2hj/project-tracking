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
import org.springframework.beans.factory.annotation.Autowired;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.messages.Messages;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectsChangeStatusContext;
import szakdolgozat.project_tracking.manager.ProjectManager;
import szakdolgozat.project_tracking.utilities.ProjectNotFoundException;

/**
 * Unit tests for {@link ProjectServiceHandler} verifying event handler logic in
 * isolation.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceHandlerTest {

    @Mock
    private ProjectManager projectManager;

    @Mock
    private Messages messages;

    @Autowired
    private ProjectServiceHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ProjectServiceHandler(projectManager);
        // Inject the mocked Messages instance into the handler. The field is package
        // scoped,
        // so tests can set it directly.
        handler.messages = messages;
    }

    /**
     * Verifies that the handler delegates a status change to the project manager
     * and sets the resulting project on the context when the manager succeeds. No
     * error message should be emitted and the fallback resolution should not be
     * called.
     */
    @Test
    void onChangeStatus_shouldSetResult_whenProjectFound() throws Exception {
        ProjectsChangeStatusContext context = mock(ProjectsChangeStatusContext.class);
        Projects updated = Projects.create();
        // Mock the manager to return a project for the change request
        when(projectManager.changeStatus(context)).thenReturn(updated);

        handler.onChangeStatus(context);

        verify(projectManager).changeStatus(context);
        verify(context).setResult(updated);
        verify(projectManager, never()).projectByContext(any());
        verify(messages, never()).error(anyString());
    }

    /**
     * Ensures that when the project manager throws a
     * {@link ProjectNotFoundException} the handler fetches the original project and
     * returns it, while emitting an error message. This mimics the fallback logic
     * described in the handler implementation.
     */
    @Test
    void onChangeStatus_shouldHandleProjectNotFoundException() throws Exception {
        ProjectsChangeStatusContext context = org.mockito.Mockito.mock(ProjectsChangeStatusContext.class);
        Projects original = Projects.create();
        ProjectNotFoundException exception = new ProjectNotFoundException("Project not found");

        // Simulate the manager throwing when asked to change the status
        when(projectManager.changeStatus(context)).thenThrow(exception);
        // Simulate resolving the project when falling back
        when(projectManager.projectByContext(context)).thenReturn(original);

        handler.onChangeStatus(context);

        verify(projectManager).changeStatus(context);
        verify(projectManager).projectByContext(context);
        verify(context).setResult(original);
        verify(messages).error("Project not found");
    }

    /**
     * Tests the after-read handler to ensure it delegates to the project manager to
     * mark status fields read‑only and sets a result on the context. The actual
     * result object is created via {@link com.sap.cds.ResultBuilder}, but we only
     * assert that it is provided to the context.
     */
    @Test
    void afterRead_shouldMarkStatusReadOnly_andSetResult() {
        CdsReadEventContext context = mock(CdsReadEventContext.class);
        Projects p1 = Projects.create();
        p1.setHasActiveEntity(true);
        Projects p2 = Projects.create();
        p2.setHasActiveEntity(false);
        List<Projects> projects = Arrays.asList(p1, p2);

        handler.afterRead(context, projects);

        verify(projectManager).setStatusFieldToReadOnly(projects);
        verify(context).setResult(any());
    }
}