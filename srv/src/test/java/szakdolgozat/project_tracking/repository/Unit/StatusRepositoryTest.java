package szakdolgozat.project_tracking.repository.Unit;

import cds.gen.szakdolgozat.db.models.core.Status;
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
import szakdolgozat.project_tracking.repository.StatusRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class StatusRepositoryTest {

    @Mock
    private PersistenceService db;

    @InjectMocks
    private StatusRepository statusRepository;

    /**
     * Verifies the repository creates a select query and delegates it to the persistence service.
     */
    @Test
    void testGetStatusById_CallsDbRunWithCorrectSelect() {
        String statusId = "statusid1";
        Result mockResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Result result = statusRepository.getStatusById(statusId);

        ArgumentCaptor<CqnSelect> captor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(captor.capture());
        CqnSelect select = captor.getValue();

        assertNotNull(select);
        assertEquals(mockResult, result);
        assertTrue(select.ref().toString().contains(Status.class.getSimpleName()));
    }

    /**
     * Ensures the repository returns exactly the Result instance provided by the persistence service.
     */
    @Test
    void testGetStatusById_ReturnsResultFromDb() {
        String statusId = "statusid2";
        Result expectedResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(expectedResult);

        Result actualResult = statusRepository.getStatusById(statusId);

        assertSame(expectedResult, actualResult);
        verify(db, times(1)).run(any(CqnSelect.class));
    }

    /**
     * Returns an empty result when the persistence service does not find a status for the provided ID.
     */
    @Test
    void testGetStatusById_WithUnknownIdReturnsEmptyResult() {
        String statusId = "unknown-status";
        Result emptyResult = mock(Result.class);
        when(emptyResult.single()).thenReturn(null);
        when(db.run(any(CqnSelect.class))).thenReturn(emptyResult);

        Result actualResult = statusRepository.getStatusById(statusId);

        assertNotNull(actualResult);
        assertNull(actualResult.single());
        verify(db, times(1)).run(any(CqnSelect.class));
    }
}
