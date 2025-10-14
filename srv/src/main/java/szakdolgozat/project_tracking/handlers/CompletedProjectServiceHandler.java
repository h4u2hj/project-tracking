package szakdolgozat.project_tracking.handlers;


import cds.gen.szakdolgozat.srv.service.completedprojectservice.CompletedProjectService_;
import cds.gen.szakdolgozat.srv.service.completedprojectservice.Projects_;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectsChangeStatusContext;
import com.sap.cds.ResultBuilder;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.manager.ProjectManager;
import szakdolgozat.project_tracking.utilities.ProjectNotFoundException;

import java.util.List;

@Component
@ServiceName(CompletedProjectService_.CDS_NAME)
public class CompletedProjectServiceHandler implements EventHandler {

    private final ProjectManager projectManager;
    @Autowired
    Messages messages;

    @Autowired
    public CompletedProjectServiceHandler(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

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

    @After(event = CqnService.EVENT_READ, entity = Projects_.CDS_NAME)
    public void afterRead(CdsReadEventContext context, List<Projects> projects) {
        projectManager.setStatusFieldToReadOnly(projects);
        context.setResult(ResultBuilder.selectedRows(projects).inlineCount(projects.size()).result());
    }
}
