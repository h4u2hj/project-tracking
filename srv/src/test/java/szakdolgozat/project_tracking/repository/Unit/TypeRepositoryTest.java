package szakdolgozat.project_tracking.repository.Unit;

import cds.gen.szakdolgozat.db.models.core.Type;
import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import szakdolgozat.project_tracking.repository.TypeRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TypeRepositoryTest {

    @Mock
    private PersistenceService db;

    @InjectMocks
    private TypeRepository typeRepository;

    /**
     * Verifies the repository creates a select query for the Type entity and delegates it to the persistence service.
     */
    @Test
    void testGetTypeById_CallsDbRunWithCorrectSelect() {
        String typeId = "typeid1";
        Result mockResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Result result = typeRepository.getTypeById(typeId);

        ArgumentCaptor<CqnSelect> captor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(captor.capture());
        CqnSelect select = captor.getValue();

        assertNotNull(select);
        assertEquals(mockResult, result);
        assertTrue(select.ref().toString().contains(Type.class.getSimpleName()));
    }

    /**
     * Ensures the repository returns exactly the Result instance provided by the persistence service.
     */
    @Test
    void testGetTypeById_ReturnsResultFromDb() {
        String typeId = "typeid2";
        Result expectedResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(expectedResult);

        Result actualResult = typeRepository.getTypeById(typeId);

        assertSame(expectedResult, actualResult);
        verify(db, times(1)).run(any(CqnSelect.class));
    }

    /**
     * Returns an empty result when the persistence service does not find a type for the provided ID.
     */
    @Test
    void testGetTypeById_WithUnknownIdReturnsEmptyResult() {
        String typeId = "unknown-type";
        Result emptyResult = mock(Result.class);
        when(emptyResult.single()).thenReturn(null);
        when(db.run(any(CqnSelect.class))).thenReturn(emptyResult);

        Result actualResult = typeRepository.getTypeById(typeId);

        assertNotNull(actualResult);
        assertNull(actualResult.single());
        verify(db, times(1)).run(any(CqnSelect.class));
    }
}
