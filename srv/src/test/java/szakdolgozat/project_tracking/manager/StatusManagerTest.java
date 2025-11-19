package szakdolgozat.project_tracking.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sap.cds.Result;
import com.sap.cds.Row;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import szakdolgozat.project_tracking.repository.StatusRepository;

/**
 * Unit tests for {@link StatusManager} verifying simple enrichment methods.
 */
@ExtendWith(MockitoExtension.class)
class StatusManagerTest {

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private Result result;

    private StatusManager statusManager;

    @BeforeEach
    void setUp() {
        statusManager = new StatusManager(statusRepository);
    }

    /**
     * Ensures total projects are refreshed using the repository row count.
     */
    @Test
    void updateTotalProjects_populatesCountFromRepository() {
        Status status = Status.create();
        status.setId("status-id");
        when(statusRepository.selectProjectsByStatusId("status-id")).thenReturn(result);
        when(result.rowCount()).thenReturn(3L);

        statusManager.updateTotalProjects(status);

        assertEquals(3, status.getTotalProjects());
        verify(statusRepository).selectProjectsByStatusId("status-id");
    }

    /**
     * Skips repository access when the status identifier is missing.
     */
    @Test
    void updateTotalProjects_skipsWhenIdMissing() {
        Status status = Status.create();

        statusManager.updateTotalProjects(status);

        verify(statusRepository, never()).selectProjectsByStatusId(null);
    }

    /**
     * Sets delete flag when no projects are linked to the status.
     */
    @Test
    void updateDeleteAc_setsTrueWhenNoProjects() {
        Status status = Status.create();
        status.setId("status-id");
        when(statusRepository.selectProjectsByStatusId("status-id")).thenReturn(result);
        when(result.stream()).thenReturn(Stream.empty());

        statusManager.updateDeleteAc(status);

        verify(statusRepository).selectProjectsByStatusId("status-id");
        assertEquals(true, status.getDeleteAc());
    }

    /**
     * Keeps delete disabled when projects still reference the status.
     */
    @Test
    void updateDeleteAc_setsFalseWhenProjectsExist() {
        Status status = Status.create();
        status.setId("status-id");
        when(statusRepository.selectProjectsByStatusId("status-id")).thenReturn(result);
        when(result.stream()).thenReturn(Stream.of(mock(Row.class)));

        statusManager.updateDeleteAc(status);

        verify(statusRepository).selectProjectsByStatusId("status-id");
        assertEquals(false, status.getDeleteAc());
    }
}
