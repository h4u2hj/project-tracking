package szakdolgozat.project_tracking.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.db.models.core.Status_;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;

@Component
public class StatusRepository {
    @Autowired
    PersistenceService db;

    /**
     * Loads a status record by its id.
     *
     * @param statusId status identifier
     * @return result containing the status record
     */
    public Result getStatusById(String statusId) {
        CqnSelect select = Select.from(Status_.class).byId(statusId);
        return db.run(select);
    }

    /**
     * Lists projects using the given status.
     *
     * @param statusId status identifier
     * @return result containing matching projects
     */
    public Result selectProjectsByStatusId(String statusId) {
        CqnSelect select = Select.from(Projects_.class).where(x -> x.status_ID().eq(statusId));
        return db.run(select);
    }

    /**
     * Checks if the status is marked as final.
     *
     * @param statusId status identifier
     * @return {@code true} when the status is final
     */
    public Boolean getFinalStatusByStatusId(String statusId) {
        CqnSelect select = Select.from(Status_.class).columns("isFinalStatus").where(x -> x.ID().eq(statusId));
        Result result = db.run(select);
        String finalState = result.single(Map.class).get("isFinalStatus").toString();
        return Boolean.parseBoolean(finalState);
    }
}
