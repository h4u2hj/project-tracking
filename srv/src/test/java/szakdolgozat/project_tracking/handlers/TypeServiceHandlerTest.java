package szakdolgozat.project_tracking.handlers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import szakdolgozat.project_tracking.manager.TypeManager;

/**
 * Unit tests for {@link TypeServiceHandler} covering read enrichment behavior.
 */
@ExtendWith(MockitoExtension.class)
class TypeServiceHandlerTest {

    @Mock
    private TypeManager typeManager;

    private TypeServiceHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TypeServiceHandler(typeManager);
    }

    /**
     * Verifies that counts and delete flags are updated for each type read.
     */
    @Test
    void onReadType_shouldUpdateCountsAndDeleteFlag_forEachType() {
        Type first = Type.create();
        Type second = Type.create();

        handler.onReadType(Stream.of(first, second));

        verify(typeManager, times(1)).updateTotalFinishedProjects(first);
        verify(typeManager, times(1)).updateTotalWorkingProjects(first);
        verify(typeManager, times(1)).updateDeleteAc(first);
        verify(typeManager, times(1)).updateTotalFinishedProjects(second);
        verify(typeManager, times(1)).updateTotalWorkingProjects(second);
        verify(typeManager, times(1)).updateDeleteAc(second);
    }
}
