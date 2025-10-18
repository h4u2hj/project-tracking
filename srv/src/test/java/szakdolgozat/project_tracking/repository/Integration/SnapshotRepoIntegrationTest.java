package szakdolgozat.project_tracking.repository.Integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.sap.cds.Result;

import szakdolgozat.project_tracking.repository.SnapshotRepository;

@SpringBootTest
@ActiveProfiles("test")
class SnapshotRepoIntegrationTest {

        private static final String PROJECT_ID = "35065627-febf-4c76-92be-cf3c38b718c9";
        private static final Set<String> SNAPSHOT_IDS = Set.of(
                        "30287750-1b64-4376-8468-4b4bdfecefb7",
                        "30287751-d02c-462e-b2fa-ac064c0ca468");

        @Autowired
        private SnapshotRepository snapshotRepository;

        /**
         * Ensures the repository resolves the persisted snapshots for the provided
         * project identifier.
         */
        @Test
        void testGetSnapshotByProjectIdReturnsPersistedRows() {
                Result result = snapshotRepository.getSnapshotByProjectId(PROJECT_ID);

                assertNotNull(result, "The persistence service should return a Result instance.");

                List<Map<String, Object>> snapshots = result.listOf(Map.class).stream()
                                .map(entry -> (Map<String, Object>) entry)
                                .collect(Collectors.toList());

                assertNotNull(snapshots, "The snapshot list should not be null.");
                assertFalse(snapshots.isEmpty(), "Snapshots should be returned for the known project.");
                assertEquals(2, snapshots.size(), "Exactly two snapshots should be persisted for the project.");

                snapshots.forEach(snapshot -> {
                        assertEquals(PROJECT_ID, snapshot.get("project_ID"),
                                        "Each snapshot entry should belong to the requested project.");
                        assertEquals("65891071-3d68-42ff-981b-988f6a2bc33b", snapshot.get("status_ID"),
                                        "Each snapshot should reference the expected status identifier.");
                });

                Set<String> retrievedIds = snapshots.stream()
                                .map(snapshot -> (String) snapshot.get("ID"))
                                .collect(Collectors.toSet());

                assertEquals(SNAPSHOT_IDS, retrievedIds,
                                "The repository should return the persisted snapshot identifiers.");
        }
}
