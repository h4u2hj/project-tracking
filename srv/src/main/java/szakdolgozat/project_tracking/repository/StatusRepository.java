package szakdolgozat.project_tracking.repository;

import cds.gen.szakdolgozat.db.models.core.Status_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;

@Component
public class StatusRepository {
    @Autowired
    PersistenceService db;

    /**
     * Retrieves the status entity from the database by its ID.
     *
     * @param statusId the ID of the status to retrieve
     * @return a {@link Result} containing the status entity if found, or empty if not found
     */
    public Result getStatusById(String statusId) {
        CqnSelect select = Select.from(Status_.class).byId(statusId);
        return db.run(select);
    }

    /**
     * Retrieves projects that have the specified status ID.
     *
     * @param statusId the ID of the status to filter projects by
     * @return a {@link Result} containing the projects with the given status ID
     */
    public Result selectProjectsByStatusId(String statusId) {
        CqnSelect select = Select.from(Projects_.class).where(x -> x.status_ID().eq(statusId));
        return db.run(select);
    }
}
