package szakdolgozat.project_tracking.handlers;

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import cds.gen.szakdolgozat.srv.service.typeservice.TypeService_;
import cds.gen.szakdolgozat.srv.service.typeservice.Type_;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.manager.TypeManager;

import java.util.stream.Stream;

@Component
@ServiceName(TypeService_.CDS_NAME)
public class TypeServiceHandler implements EventHandler {

    private final TypeManager typeManager;

    @Autowired
    public TypeServiceHandler(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    @After(event = CqnService.EVENT_READ, entity = Type_.CDS_NAME)
    public void onReadType(Stream<Type> types) {
        types.forEach(type -> {
            typeManager.updateTotalFinishedProjects(type);
            typeManager.updateTotalWorkingProjects(type);
            typeManager.updateDeleteAc(type);
        });
    }
}