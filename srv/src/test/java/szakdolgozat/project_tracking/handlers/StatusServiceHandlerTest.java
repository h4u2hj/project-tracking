package szakdolgozat.project_tracking.handlers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import szakdolgozat.project_tracking.manager.StatusManager;

/**
 * Unit tests for {@link StatusServiceHandler} verifying read enrichment logic.
 */
@ExtendWith(MockitoExtension.class)
class StatusServiceHandlerTest {

    @Mock
    private StatusManager statusManager;

    private StatusServiceHandler handler;

    @BeforeEach
    void setUp() {
        handler = new StatusServiceHandler(statusManager);
    }

    /**
     * Ensures totals and delete flag are updated for every status in the stream.
     */
    @Test
    void onReadStatus_shouldUpdateTotalsAndDeleteFlag_forEachStatus() {
        Status first = Status.create();
        Status second = Status.create();

        handler.onReadStatus(Stream.of(first, second));

        verify(statusManager, times(1)).updateTotalProjects(first);
        verify(statusManager, times(1)).updateDeleteAc(first);
        verify(statusManager, times(1)).updateTotalProjects(second);
        verify(statusManager, times(1)).updateDeleteAc(second);
    }
}
