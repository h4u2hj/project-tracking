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

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import szakdolgozat.project_tracking.repository.TypeRepository;

/**
 * Unit tests for {@link TypeManager} focusing on simple counter updates.
 */
@ExtendWith(MockitoExtension.class)
class TypeManagerTest {

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private Result finishedResult;

    @Mock
    private Result workingResult;

    @Mock
    private Result deleteResult;

    private TypeManager typeManager;

    @BeforeEach
    void setUp() {
        typeManager = new TypeManager(typeRepository);
    }

    /**
     * Ensures finished project totals are populated from repository counts.
     */
    @Test
    void updateTotalFinishedProjects_populatesCountFromRepository() {
        Type type = Type.create();
        type.setId("type-id");
        when(typeRepository.selectFinishedProjectsByTypeId("type-id")).thenReturn(finishedResult);
        when(finishedResult.rowCount()).thenReturn(5L);

        typeManager.updateTotalFinishedProjects(type);

        assertEquals(5, type.getTotalFinishedProjects());
        verify(typeRepository).selectFinishedProjectsByTypeId("type-id");
    }

    /**
     * Ensures working project totals are populated from repository counts.
     */
    @Test
    void updateTotalWorkingProjects_populatesCountFromRepository() {
        Type type = Type.create();
        type.setId("type-id");
        when(typeRepository.selectWorkingProjectsByTypeId("type-id")).thenReturn(workingResult);
        when(workingResult.rowCount()).thenReturn(2L);

        typeManager.updateTotalWorkingProjects(type);

        assertEquals(2, type.getTotalInProgressProjects());
        verify(typeRepository).selectWorkingProjectsByTypeId("type-id");
    }

    /**
     * Sets delete flag when no projects reference the type.
     */
    @Test
    void updateDeleteAc_setsTrueWhenNoReferencedProjects() {
        Type type = Type.create();
        type.setId("type-id");
        when(typeRepository.selectProjectsByTypeId("type-id")).thenReturn(deleteResult);
        when(deleteResult.stream()).thenReturn(Stream.empty());

        typeManager.updateDeleteAc(type);

        verify(typeRepository).selectProjectsByTypeId("type-id");
        assertEquals(true, type.getDeleteAc());
    }

    /**
     * Leaves delete flag false when projects reference the type.
     */
    @Test
    void updateDeleteAc_setsFalseWhenReferencedProjectsExist() {
        Type type = Type.create();
        type.setId("type-id");
        when(typeRepository.selectProjectsByTypeId("type-id")).thenReturn(deleteResult);
        when(deleteResult.stream()).thenReturn(Stream.of(mock(Row.class)));

        typeManager.updateDeleteAc(type);

        verify(typeRepository).selectProjectsByTypeId("type-id");
        assertEquals(false, type.getDeleteAc());
    }

    /**
     * Skips repository lookups when the type identifier is null.
     */
    @Test
    void updateMethods_skipWhenIdMissing() {
        Type type = Type.create();

        typeManager.updateTotalFinishedProjects(type);
        typeManager.updateTotalWorkingProjects(type);
        typeManager.updateDeleteAc(type);

        verify(typeRepository, never()).selectFinishedProjectsByTypeId(null);
        verify(typeRepository, never()).selectWorkingProjectsByTypeId(null);
        verify(typeRepository, never()).selectProjectsByTypeId(null);
    }
}
