package szakdolgozat.project_tracking.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.db.models.core.Snapshot;
import cds.gen.szakdolgozat.db.models.core.Snapshot_;

@Component
public class SnapshotRepository {

    @Autowired
    PersistenceService db;

    /**
     * Stores a snapshot of the project's previous status.
     *
     * @param formerStatusId status used before the change
     * @param projectId      project identifier
     */
    public void createSnapshot(String formerStatusId, String projectId) {
        Snapshot snapshot = Snapshot.create();
        snapshot.setStatusId(formerStatusId);
        snapshot.setProjectId(projectId);
        CqnInsert insert = Insert.into(Snapshot_.class).entry(snapshot);
        db.run(insert);
    }

    /**
     * Retrieves snapshots for the given project.
     *
     * @param projectId project identifier
     * @return result containing the matching snapshots
     */
    public Result getSnapshotByProjectId(String projectId) {
        CqnSelect select = Select.from(Snapshot_.class).where(x -> x.project_ID().eq(projectId));
        return db.run(select);
    }
}
