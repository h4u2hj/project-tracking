package szakdolgozat.project_tracking.manager;

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import com.sap.cds.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.repository.TypeRepository;

@Component
public class TypeManager {
    private final TypeRepository typeRepository;

    @Autowired
    public TypeManager(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public void updateTotalFinishedProjects(Type type) {
        String typeId = type.getId();

        if (typeId == null) {
            return;
        }

        int projectNumberOfType = calculateFinishedProjectByType(typeId);
        type.setTotalFinishedProjects(projectNumberOfType);
    }

    private int calculateFinishedProjectByType(String typeId) {
        Result rows = typeRepository.selectFinishedProjectsByTypeId(typeId);
        return (int) rows.rowCount();
    }

    public void updateTotalWorkingProjects(Type type) {
        String typeId = type.getId();

        if (typeId == null) {
            return;
        }

        int projectNumberOfType = calculateWorkingProjectByType(typeId);
        type.setTotalInProgressProjects(projectNumberOfType);
    }

    private int calculateWorkingProjectByType(String typeId) {
        Result rows = typeRepository.selectWorkingProjectsByTypeId(typeId);
        return (int) rows.rowCount();
    }

    public void updateDeleteAc(Type type) {
        String typeId = type.getId();
        if (typeId == null) {
            return;
        }

        boolean newDeleteAc = evaluateDeleteAc(typeId);
        type.setDeleteAc(newDeleteAc);
    }

    private boolean evaluateDeleteAc(String typeId) {
        Result status = typeRepository.selectProjectsByTypeId(typeId);
        return status.stream().findAny().isEmpty();
    }
}
