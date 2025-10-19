package szakdolgozat.project_tracking.handlers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import cds.gen.szakdolgozat.srv.service.statusservice.StatusService_;
import cds.gen.szakdolgozat.srv.service.statusservice.Status_;
import szakdolgozat.project_tracking.manager.StatusManager;

@Component
@ServiceName(StatusService_.CDS_NAME)
public class StatusServiceHandler implements EventHandler {

    private final StatusManager statusManager;

    /**
     * Creates the handler with the status manager dependency.
     *
     * @param statusManager manager handling status enrichments
     */
    public StatusServiceHandler(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    /**
     * Counts and updates the total projects connected to the status every read.
     *
     * @param statuses stream of status records from the read
     */
    @After(event = CqnService.EVENT_READ, entity = Status_.CDS_NAME)
    public void onReadStatus(Stream<Status> statuses) {
        statuses.forEach(status -> {
            statusManager.updateTotalProjects(status);
            statusManager.updateDeleteAc(status);
        });
    }
}
