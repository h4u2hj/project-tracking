package szakdolgozat.project_tracking.repository.Integration;

import com.sap.cds.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import szakdolgozat.project_tracking.repository.TypeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class TypeRepoPersistenceIntegrationTest {
    private static final String ACTUAL_TYPE_ID = "9823010b-fd13-4ade-a14f-269ce0638c82";

    @Autowired
    TypeRepository typeRepository;

    /**
     * Verifies the repository fetches the expected type from the persistence layer.
     */
    @Test
    public void testGetTypeFromDB() {
        Result result = typeRepository.getTypeById(ACTUAL_TYPE_ID);

        assertNotNull(result, "The persistence service should return a Result instance.");
        assertEquals(ACTUAL_TYPE_ID, result.single().get("ID"));
        assertEquals("Coding", result.single().get("name"));
    }
}
