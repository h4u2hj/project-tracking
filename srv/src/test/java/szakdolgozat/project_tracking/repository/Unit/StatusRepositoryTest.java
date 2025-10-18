package szakdolgozat.project_tracking.repository.Unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.szakdolgozat.db.models.core.Project;
import cds.gen.szakdolgozat.db.models.core.Status;
import szakdolgozat.project_tracking.repository.StatusRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class StatusRepositoryTest {

    @Mock
    private PersistenceService db;

    @InjectMocks
    private StatusRepository statusRepository;

    /**
     * Verifies the repository creates a select query and delegates it to the
     * persistence service.
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
     * Ensures the repository returns exactly the Result instance provided by the
     * persistence service.
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
     * Returns an empty result when the persistence service does not find a status
     * for the provided ID.
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

    /**
     * Checks that project selection delegates to the persistence service with a
     * projects query.
     */
    @Test
    void testSelectProjectsByStatusId_CallsDbRunWithProjectsSelect() {
        String statusId = "status-project";
        Result mockResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Result actualResult = statusRepository.selectProjectsByStatusId(statusId);

        ArgumentCaptor<CqnSelect> captor = ArgumentCaptor.forClass(CqnSelect.class);
        verify(db).run(captor.capture());
        CqnSelect select = captor.getValue();

        assertNotNull(select);
        assertTrue(select.ref().toString().contains(Project.class.getSimpleName()));
        assertSame(mockResult, actualResult);
    }

    /**
     * Ensures selectProjectsByStatusId returns the persistence layer Result.
     */
    @Test
    void testSelectProjectsByStatusId_ReturnsResultFromDb() {
        String statusId = "status-project-return";
        Result expectedResult = mock(Result.class);
        when(db.run(any(CqnSelect.class))).thenReturn(expectedResult);

        Result actualResult = statusRepository.selectProjectsByStatusId(statusId);

        assertSame(expectedResult, actualResult);
        verify(db, times(1)).run(any(CqnSelect.class));
    }

    /**
     * Validates final status retrieval parses the SAP CAP boolean flag correctly.
     */
    @Test
    void testGetFinalStatusByStatusId_ReturnsTrueWhenFlagSet() {
        String statusId = "final-status";
        Result mockResult = mock(Result.class);
        when(mockResult.single(Map.class)).thenReturn(Map.of("isFinalStatus", "true"));
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Boolean finalStatus = statusRepository.getFinalStatusByStatusId(statusId);

        assertTrue(finalStatus);
        verify(db, times(1)).run(any(CqnSelect.class));
        verify(mockResult, times(1)).single(Map.class);
    }

    /**
     * Ensures false flags from the persistence service are translated to a boolean
     * false.
     */
    @Test
    void testGetFinalStatusByStatusId_ReturnsFalseWhenFlagUnset() {
        String statusId = "non-final-status";
        Result mockResult = mock(Result.class);
        when(mockResult.single(Map.class)).thenReturn(Map.of("isFinalStatus", "false"));
        when(db.run(any(CqnSelect.class))).thenReturn(mockResult);

        Boolean finalStatus = statusRepository.getFinalStatusByStatusId(statusId);

        assertFalse(finalStatus);
        verify(db, times(1)).run(any(CqnSelect.class));
        verify(mockResult, times(1)).single(Map.class);
    }
}
