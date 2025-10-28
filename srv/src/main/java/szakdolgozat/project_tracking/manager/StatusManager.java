package szakdolgozat.project_tracking.manager;

import org.springframework.stereotype.Component;

import com.sap.cds.Result;

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import szakdolgozat.project_tracking.repository.StatusRepository;

@Component
public class StatusManager {
    private final StatusRepository statusRepository;

    /**
     * Builds the status manager with its repository.
     *
     * @param statusRepository repository for status data
     */
    public StatusManager(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    /**
     * Refreshes the total project count on the status entity.
     *
     * @param status status entity to update
     */
    public void updateTotalProjects(Status status) {
        String statusId = status.getId();

        if (statusId == null) {
            return;
        }

        int projectNumberOfStatus = calculateProjectByStatus(statusId);
        status.setTotalProjects(projectNumberOfStatus);
    }

    /**
     * Counts how many projects use the given status.
     *
     * @param statusId status identifier
     * @return number of related projects
     */
    private int calculateProjectByStatus(String statusId) {
        Result rows = statusRepository.selectProjectsByStatusId(statusId);
        return (int) rows.rowCount();
    }

    /**
     * Updates the delete capability flag for the status entity.
     *
     * @param status status entity to update
     */
    public void updateDeleteAc(Status status) {
        String statusId = status.getId();
        if (statusId == null) {
            return;
        }

        boolean newDeleteAc = evaluateDeleteAc(statusId);
        status.setDeleteAc(newDeleteAc);
    }

    /**
     * Determines if the status can be deleted.
     *
     * @param statusId status identifier
     * @return {@code true} if no project references the status
     */
    private boolean evaluateDeleteAc(String statusId) {
        Result status = statusRepository.selectProjectsByStatusId(statusId);
        return status.stream().findAny().isEmpty();
    }
}
