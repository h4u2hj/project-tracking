package szakdolgozat.project_tracking.repository;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;

@Component
public class ProjectRepository {

    @Autowired
    PersistenceService db;

    public void updateProjectStatus(String projectId, String newStatusId, Timestamp timeOfChange) {
        CqnUpdate update;

        update = Update.entity(Projects_.class).data("status_ID", newStatusId).where(d -> d.ID().eq(projectId));
        db.run(update);

        update = Update.entity(Projects_.class).data("lastStatusChangeAt", timeOfChange).where(d -> d.ID().eq(projectId));
        db.run(update);
    }

    public void setCompletedTime(String projectId, Timestamp timeOfCompletion) {
        CqnUpdate update = Update.entity(Projects_.class).data("completedAt", timeOfCompletion).where(d -> d.ID().eq(projectId));
        db.run(update);
    }

    public String statusIdByProjectId(String projectId) {
        CqnSelect select = Select.from(Projects_.class).columns("status_ID").where(d -> d.ID().eq(projectId));

        Result result = db.run(select);
        return result.single(Map.class).get("status_ID").toString();
    }

    public Projects projectById(String projectId) {
        CqnSelect select = Select.from(Projects_.class).where(d -> d.ID().eq(projectId));
        return db.run(select).single(Projects.class);
    }
}
