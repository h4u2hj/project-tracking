package szakdolgozat.project_tracking.handlers;

import cds.gen.szakdolgozat.srv.service.projectservice.ProjectService_;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;
import com.sap.cds.ResultBuilder;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.manager.ProjectManager;

import java.util.List;

@Component
@ServiceName(ProjectService_.CDS_NAME)
public class ProjectServiceHandler implements EventHandler {

    private final ProjectManager projectManager;
    @Autowired
    Messages messages;

    @Autowired
    public ProjectServiceHandler(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @After(event = CqnService.EVENT_READ, entity = Projects_.CDS_NAME)
    public void afterRead(CdsReadEventContext context, List<Projects> projects) {
        projectManager.setStatusFieldToReadOnly(projects);
        context.setResult(ResultBuilder.selectedRows(projects).inlineCount(projects.size()).result());
    }
}
