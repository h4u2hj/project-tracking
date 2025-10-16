package szakdolgozat.project_tracking.repository.Integration;

import com.sap.cds.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import szakdolgozat.project_tracking.repository.StatusRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class StatusRepoPersistenceIntegrationTest {

    private static final String STATUS_ID = "65891071-3d68-42ff-981b-988f6a2bc33b";

    @Autowired
    private StatusRepository statusRepository;

    /**
     * Confirms the repository returns the persisted status entity for the provided identifier.
     */
    @Test
    void testGetStatusFromDB() {
        Result result = statusRepository.getStatusById(STATUS_ID);
        assertNotNull(result, "The persistence service should return a Result instance.");

        assertEquals(STATUS_ID, result.single().get("ID"));
        assertEquals("In development", result.single().get("name"));
    }

    /**
     * Verifies the repository correctly resolves the isFinalStatus flag for the persisted status.
     */
    @Test
    void testGetFinalStatusByStatusId() {
        Boolean finalStatus = statusRepository.getFinalStatusByStatusId(STATUS_ID);

        assertNotNull(finalStatus, "The final status flag should not be null.");
        assertFalse(finalStatus, "The selected status should not be marked as final in the persistence layer.");
    }
}
