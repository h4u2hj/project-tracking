package szakdolgozat.project_tracking.handlers;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import cds.gen.szakdolgozat.srv.service.typeservice.TypeService_;
import cds.gen.szakdolgozat.srv.service.typeservice.Type_;
import szakdolgozat.project_tracking.manager.TypeManager;

@Component
@ServiceName(TypeService_.CDS_NAME)
public class TypeServiceHandler implements EventHandler {

    private final TypeManager typeManager;

    /**
     * Creates the handler with the type manager dependency.
     *
     * @param typeManager manager handling type enrichments
     */
    @Autowired
    public TypeServiceHandler(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    /**
     * Counts and updates the total projects connected to the type every read.
     *
     * @param types stream of type records from the read
     */
    @After(event = CqnService.EVENT_READ, entity = Type_.CDS_NAME)
    public void onReadType(Stream<Type> types) {
        types.forEach(type -> {
            typeManager.updateTotalFinishedProjects(type);
            typeManager.updateTotalWorkingProjects(type);
            typeManager.updateDeleteAc(type);
        });
    }
}
