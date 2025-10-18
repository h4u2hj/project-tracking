package szakdolgozat.project_tracking.repository.Unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.db.models.core.Snapshot;
import szakdolgozat.project_tracking.repository.SnapshotRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SnapshotRepositoryTest {

    @Mock
    private PersistenceService db;

    @InjectMocks
    private SnapshotRepository snapshotRepository;

    /**
     * Verifies the repository issues an insert statement targeting the snapshot
     * entity.
     */
    @Test
    void testCreateSnapshot_DelegatesInsertToPersistenceService() {
        String statusId = "status-1234";
        String projectId = "project-4567";

        ArgumentCaptor<CqnInsert> insertCaptor = ArgumentCaptor.forClass(CqnInsert.class);

        snapshotRepository.createSnapshot(statusId, projectId);

        verify(db, times(1)).run(insertCaptor.capture());
        CqnInsert insert = insertCaptor.getValue();

        assertNotNull(insert, "The repository should dispatch a CQN insert statement.");
        assertTrue(insert.toString().contains(Snapshot.class.getSimpleName()),
                "The insert should target the Snapshot entity.");
        assertTrue(insert.toString().contains(statusId),
                "The inserted snapshot should retain the provided status identifier.");
        assertTrue(insert.toString().contains(projectId),
                "The inserted snapshot should retain the provided project identifier.");
    }

    /**
     * Ensures the repository constructs a select statement for retrieving snapshots
     * by project ID.
     */
    @Test
    void testGetSnapshotByProjectId_IssuesSelectAgainstSnapshots() {
        String projectId = "project-select";
        Result mockResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        snapshotRepository.getSnapshotByProjectId(projectId);

        ArgumentCaptor<CqnSelect> selectCaptor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(selectCaptor.capture());

        CqnSelect select = selectCaptor.getValue();
        assertNotNull(select, "A select statement should be created.");
        assertTrue(select.ref().toString().contains(Snapshot.class.getSimpleName()),
                "The select statement should reference the Snapshot entity.");
    }

    /**
     * Confirms the repository returns the Result instance provided by the
     * persistence layer.
     */
    @Test
    void testGetSnapshotByProjectId_ReturnsResultFromPersistence() {
        String projectId = "project-results";
        Result expectedResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(expectedResult);

        Result actualResult = snapshotRepository.getSnapshotByProjectId(projectId);

        assertSame(expectedResult, actualResult, "The repository should not alter the persistence Result.");
        verify(db, times(1)).run(any(CqnSelect.class));
    }
}
