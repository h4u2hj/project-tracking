package szakdolgozat.project_tracking.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.db.models.core.Type_;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;

@Component
public class TypeRepository {

    @Autowired
    PersistenceService db;

    /**
     * Loads a type record by its id.
     *
     * @param id type identifier
     * @return result containing the type record
     */
    public Result getTypeById(String id) {
        CqnSelect select = Select.from(Type_.class).byId(id);
        return db.run(select);
    }

    /**
     * Lists finished projects for the given type.
     *
     * @param id type identifier
     * @return result containing finished projects
     */
    public Result selectFinishedProjectsByTypeId(String id) {
        CqnSelect select = Select.from(Projects_.class)
                .where(x -> x.type_ID().eq(id).and(x.status().isFinalStatus().eq(true)));
        return db.run(select);
    }

    /**
     * Lists in-progress projects for the given type.
     *
     * @param id type identifier
     * @return result containing in-progress projects
     */
    public Result selectWorkingProjectsByTypeId(String id) {
        CqnSelect select = Select.from(Projects_.class)
                .where(x -> x.type_ID().eq(id).and(x.status().isFinalStatus().eq(false)));
        return db.run(select);
    }

    /**
     * Lists all projects for the given type.
     *
     * @param typeId type identifier
     * @return result containing matching projects
     */
    public Result selectProjectsByTypeId(String typeId) {
        CqnSelect select = Select.from(Projects_.class).where(x -> x.type_ID().eq(typeId));
        return db.run(select);
    }
}
