package szakdolgozat.project_tracking.repository.Unit;

import cds.gen.szakdolgozat.db.models.core.Type;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
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

    /**
     * Ensures the finished project query filters by type and final status.
     */
    @Test
    void testSelectFinishedProjectsByTypeId_CallsDbRunWithCorrectSelect() {
        String typeId = "type-finished";
        Result mockResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Result result = typeRepository.selectFinishedProjectsByTypeId(typeId);

        ArgumentCaptor<CqnSelect> captor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(captor.capture());
        CqnSelect select = captor.getValue();
        CqnSelect expected = Select.from(Projects_.class)
                .where(x -> x.type_ID().eq(typeId).and(x.status().isFinalStatus().eq(true)));

        assertNotNull(select);
        assertSame(mockResult, result);
        assertEquals(expected.toString(), select.toString());
    }

    /**
     * Ensures the finished project lookup returns the persistence result untouched.
     */
    @Test
    void testSelectFinishedProjectsByTypeId_ReturnsResultFromDb() {
        String typeId = "type-finished-2";
        Result expectedResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(expectedResult);

        Result actualResult = typeRepository.selectFinishedProjectsByTypeId(typeId);

        assertSame(expectedResult, actualResult);
        verify(db, times(1)).run(any(CqnSelect.class));
    }

    /**
     * Ensures the in-flight project query filters by type and non-final status.
     */
    @Test
    void testSelectWorkingProjectsByTypeId_CallsDbRunWithCorrectSelect() {
        String typeId = "type-working";
        Result mockResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Result result = typeRepository.selectWorkingProjectsByTypeId(typeId);

        ArgumentCaptor<CqnSelect> captor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(captor.capture());
        CqnSelect select = captor.getValue();
        CqnSelect expected = Select.from(Projects_.class)
                .where(x -> x.type_ID().eq(typeId).and(x.status().isFinalStatus().eq(false)));

        assertNotNull(select);
        assertSame(mockResult, result);
        assertEquals(expected.toString(), select.toString());
    }

    /**
     * Ensures the in-flight project lookup returns the persistence result untouched.
     */
    @Test
    void testSelectWorkingProjectsByTypeId_ReturnsResultFromDb() {
        String typeId = "type-working-2";
        Result expectedResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(expectedResult);

        Result actualResult = typeRepository.selectWorkingProjectsByTypeId(typeId);

        assertSame(expectedResult, actualResult);
        verify(db, times(1)).run(any(CqnSelect.class));
    }

    /**
     * Ensures the generic project query only filters by the referenced type.
     */
    @Test
    void testSelectProjectsByTypeId_CallsDbRunWithCorrectSelect() {
        String typeId = "type-project";
        Result mockResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Result result = typeRepository.selectProjectsByTypeId(typeId);

        ArgumentCaptor<CqnSelect> captor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(captor.capture());
        CqnSelect select = captor.getValue();
        CqnSelect expected = Select.from(Projects_.class)
                .where(x -> x.type_ID().eq(typeId));

        assertNotNull(select);
        assertSame(mockResult, result);
        assertEquals(expected.toString(), select.toString());
    }

    /**
     * Ensures the generic project lookup returns the persistence result untouched.
     */
    @Test
    void testSelectProjectsByTypeId_ReturnsResultFromDb() {
        String typeId = "type-project-2";
        Result expectedResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(expectedResult);

        Result actualResult = typeRepository.selectProjectsByTypeId(typeId);

        assertSame(expectedResult, actualResult);
        verify(db, times(1)).run(any(CqnSelect.class));
    }
}
