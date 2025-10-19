package szakdolgozat.project_tracking.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import szakdolgozat.project_tracking.repository.TypeRepository;

@Component
public class TypeManager {
    private final TypeRepository typeRepository;

    /**
     * Builds the type manager with its repository.
     *
     * @param typeRepository repository for type data
     */
    public TypeManager(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    /**
     * Refreshes the finished project count for the type.
     *
     * @param type type entity to update
     */
    public void updateTotalFinishedProjects(Type type) {
        String typeId = type.getId();

        if (typeId == null) {
            return;
        }

        int projectNumberOfType = calculateFinishedProjectByType(typeId);
        type.setTotalFinishedProjects(projectNumberOfType);
    }

    /**
     * Counts finished projects for the given type.
     *
     * @param typeId type identifier
     * @return number of finished projects
     */
    private int calculateFinishedProjectByType(String typeId) {
        Result rows = typeRepository.selectFinishedProjectsByTypeId(typeId);
        return (int) rows.rowCount();
    }

    /**
     * Refreshes the in-progress project count for the type.
     *
     * @param type type entity to update
     */
    public void updateTotalWorkingProjects(Type type) {
        String typeId = type.getId();

        if (typeId == null) {
            return;
        }

        int projectNumberOfType = calculateWorkingProjectByType(typeId);
        type.setTotalInProgressProjects(projectNumberOfType);
    }

    /**
     * Counts working projects for the given type.
     *
     * @param typeId type identifier
     * @return number of ongoing projects
     */
    private int calculateWorkingProjectByType(String typeId) {
        Result rows = typeRepository.selectWorkingProjectsByTypeId(typeId);
        return (int) rows.rowCount();
    }

    /**
     * Updates the delete capability flag for the type.
     *
     * @param type type entity to update
     */
    public void updateDeleteAc(Type type) {
        String typeId = type.getId();
        if (typeId == null) {
            return;
        }

        boolean newDeleteAc = evaluateDeleteAc(typeId);
        type.setDeleteAc(newDeleteAc);
    }

    /**
     * Determines if the type can be deleted safely.
     *
     * @param typeId type identifier
     * @return {@code true} if no project references the type
     */
    private boolean evaluateDeleteAc(String typeId) {
        Result status = typeRepository.selectProjectsByTypeId(typeId);
        return status.stream().findAny().isEmpty();
    }
}
