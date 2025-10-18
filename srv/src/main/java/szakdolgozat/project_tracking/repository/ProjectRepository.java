package szakdolgozat.project_tracking.repository;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;

@Component
public class ProjectRepository {

    @Autowired
    PersistenceService db;

    /**
     * Updates the status and last change timestamp of a project.
     *
     * @param projectId    project identifier
     * @param newStatusId  new status identifier
     * @param timeOfChange timestamp of the change
     */
    public void updateProjectStatus(String projectId, String newStatusId, Timestamp timeOfChange) {
        CqnUpdate update;

        update = Update.entity(Projects_.class).data("status_ID", newStatusId).where(d -> d.ID().eq(projectId));
        db.run(update);

        update = Update.entity(Projects_.class).data("lastStatusChangeAt", timeOfChange)
                .where(d -> d.ID().eq(projectId));
        db.run(update);
    }

    /**
     * Sets the completion timestamp on a project.
     *
     * @param projectId        project identifier
     * @param timeOfCompletion completion timestamp
     */
    public void setCompletedTime(String projectId, Timestamp timeOfCompletion) {
        CqnUpdate update = Update.entity(Projects_.class).data("completedAt", timeOfCompletion)
                .where(d -> d.ID().eq(projectId));
        db.run(update);
    }

    /**
     * Reads the status id of a project.
     *
     * @param projectId project identifier
     * @return status identifier string
     */
    public String statusIdByProjectId(String projectId) {
        CqnSelect select = Select.from(Projects_.class).columns("status_ID").where(d -> d.ID().eq(projectId));

        Result result = db.run(select);
        return result.single(Map.class).get("status_ID").toString();
    }

    /**
     * Loads a project entity by id.
     *
     * @param projectId project identifier
     * @return project entity or {@code null} if missing
     */
    public Projects projectById(String projectId) {
        CqnSelect select = Select.from(Projects_.class).where(d -> d.ID().eq(projectId));
        return db.run(select).single(Projects.class);
    }
}
