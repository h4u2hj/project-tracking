package szakdolgozat.project_tracking.repository.Integration;

import com.sap.cds.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import szakdolgozat.project_tracking.repository.StatusRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class StatusRepositoryIntegrationTest {

    private static final String ACTUAL_STATUS_ID = "65891071-3d68-42ff-981b-988f6a2bc33b";

    @Autowired
    private StatusRepository statusRepository;

    @Test
    void testGetStatusFromDB() {
        Result result = statusRepository.getStatusById(ACTUAL_STATUS_ID);
        assertNotNull(result, "The persistence service should return a Result instance.");

        assertEquals(ACTUAL_STATUS_ID, result.single().get("ID"));
        assertEquals("In development", result.single().get("name"));
    }
}
