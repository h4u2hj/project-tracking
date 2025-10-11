package szakdolgozat.project_tracking.repository;

import cds.gen.szakdolgozat.db.models.core.Snapshot;
import cds.gen.szakdolgozat.db.models.core.Snapshot_;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SnapshotRepository {

    @Autowired
    PersistenceService db;

    public void createSnapshot(String formerStatusId, String projectId) {
        Snapshot snapshot = Snapshot.create();
        snapshot.setStatusId(formerStatusId);
        snapshot.setProjectId(projectId);
        CqnInsert insert = Insert.into(Snapshot_.class).entry(snapshot);
        db.run(insert);
    }

    public Result getSnapshotByProjectId(String projectId) {
        CqnSelect select = Select.from(Snapshot_.class).where(x -> x.project_ID().eq(projectId));
        return db.run(select);
    }
}
