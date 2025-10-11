package szakdolgozat.project_tracking.handlers;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import cds.gen.szakdolgozat.srv.service.statusservice.StatusService_;
import cds.gen.szakdolgozat.srv.service.statusservice.Status_;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.manager.StatusManager;

import java.util.stream.Stream;

@Component
@ServiceName(StatusService_.CDS_NAME)
public class StatusServiceHandler implements EventHandler {

    private final StatusManager statusManager;

    @Autowired
    public StatusServiceHandler(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    @After(event = CqnService.EVENT_READ, entity = Status_.CDS_NAME)
    public void onReadStatus(Stream<Status> statuses) {
        statuses.forEach(status -> {
            statusManager.updateTotalProjects(status);
            statusManager.updateDeleteAc(status);
        });
    }
}
