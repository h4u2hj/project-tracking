package szakdolgozat.project_tracking.repository;

import cds.gen.szakdolgozat.db.models.core.Type_;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TypeRepository {

    @Autowired
    PersistenceService db;

    public Result getTypeById(String id) {
        CqnSelect select = Select.from(Type_.class).byId(id);
        return db.run(select);
    }

    public Result selectFinishedProjectsByTypeId(String id) {
        CqnSelect select = Select.from(Projects_.class).where(x -> x.type_ID().eq(id).and(x.status().isFinalStatus().eq(true)));
        return db.run(select);
    }

    public Result selectWorkingProjectsByTypeId(String id) {
        CqnSelect select = Select.from(Projects_.class).where(x -> x.type_ID().eq(id).and(x.status().isFinalStatus().eq(false)));
        return db.run(select);
    }

    public Result selectProjectsByTypeId(String typeId) {
        CqnSelect select = Select.from(Projects_.class).where(x -> x.type_ID().eq(typeId));
        return db.run(select);
    }
}
