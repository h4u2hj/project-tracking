package szakdolgozat.project_tracking.handlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ResultBuilder;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;

import cds.gen.szakdolgozat.srv.service.projectservice.ProjectService_;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectsChangeStatusContext;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;
import szakdolgozat.project_tracking.manager.ProjectManager;
import szakdolgozat.project_tracking.utilities.ProjectNotFoundException;

@Component
@ServiceName(ProjectService_.CDS_NAME)
public class ProjectServiceHandler implements EventHandler {

    private final ProjectManager projectManager;
    @Autowired
    Messages messages;

    /**
     * Creates the handler with the project manager dependency.
     *
     * @param projectManager manager handling project actions
     */
    public ProjectServiceHandler(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    /**
     * Handles status change action for projects.
     *
     * @param context change status request context
     */
    @On(entity = Projects_.CDS_NAME, event = ProjectsChangeStatusContext.CDS_NAME)
    public void onChangeStatus(ProjectsChangeStatusContext context) {
        try {
            Projects updatedProject = projectManager.changeStatus(context);
            context.setResult(updatedProject);
        } catch (ProjectNotFoundException e) {
            Projects originalProject = projectManager.projectByContext(context);
            context.setResult(originalProject);
            messages.error(e.getMessage());
        }
    }

    /**
     * Sets status availability to read only after loading projects.
     *
     * @param context  read event context
     * @param projects projects returned from the read operation
     */
    @After(event = CqnService.EVENT_READ, entity = Projects_.CDS_NAME)
    public void afterRead(CdsReadEventContext context, List<Projects> projects) {
        projectManager.setStatusFieldToReadOnly(projects);
        context.setResult(ResultBuilder.selectedRows(projects).inlineCount(projects.size()).result());
    }
}
