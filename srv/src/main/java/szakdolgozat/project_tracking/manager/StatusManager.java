package szakdolgozat.project_tracking.manager;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import com.sap.cds.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.repository.StatusRepository;

@Component
public class StatusManager {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusManager(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public void updateTotalProjects(Status status) {
        String statusId = status.getId();

        if (statusId == null) {
            return;
        }

        int projectNumberOfStatus = calculateProjectByStatus(statusId);
        status.setTotalProjects(projectNumberOfStatus);
    }

    private int calculateProjectByStatus(String statusId) {
        Result rows = statusRepository.selectProjectsByStatusId(statusId);
        return (int) rows.rowCount();
    }

    public void updateDeleteAc(Status status) {
        String statusId = status.getId();
        if (statusId == null) {
            return;
        }

        boolean newDeleteAc = evaluateDeleteAc(statusId);
        status.setDeleteAc(newDeleteAc);
    }

    private boolean evaluateDeleteAc(String statusId) {
        Result status = statusRepository.selectProjectsByStatusId(statusId);
        return status.stream().findAny().isEmpty();
    }
}
