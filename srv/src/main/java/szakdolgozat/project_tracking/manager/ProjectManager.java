package szakdolgozat.project_tracking.manager;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ServiceException;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectsChangeStatusContext;
import szakdolgozat.project_tracking.repository.ProjectRepository;
import szakdolgozat.project_tracking.repository.SnapshotRepository;
import szakdolgozat.project_tracking.repository.StatusRepository;
import szakdolgozat.project_tracking.utilities.EventContextAnalyzer;
import szakdolgozat.project_tracking.utilities.ProjectNotFoundException;

@Component
public class ProjectManager {

    private final SnapshotRepository snapshotRepository;
    private final ProjectRepository projectRepository;
    private final StatusRepository statusRepository;

    @Autowired
    private EventContextAnalyzer eventContextAnalyzer;

    /**
     * Creates a project manager with the needed repositories.
     *
     * @param snapshotRepository repository for snapshot writes
     * @param projectRepository  repository for project data
     * @param statusRepository   repository for status lookups
     */
    public ProjectManager(SnapshotRepository snapshotRepository, ProjectRepository projectRepository,
            StatusRepository statusRepository) {
        this.snapshotRepository = snapshotRepository;
        this.projectRepository = projectRepository;
        this.statusRepository = statusRepository;
    }

    /**
     * Resolves the project referenced by the change status context.
     *
     * @param context change status request context
     * @return the matching project or {@code null} if none found
     */
    public Projects projectByContext(ProjectsChangeStatusContext context) {
        var targetKeys = eventContextAnalyzer.targetKeys(context, ctx -> ctx.getCqn().ref());
        var projectId = targetKeys.get(Projects.ID);
        if (projectId == null) {
            return null;
        }

        return projectById(projectId.toString());
    }

    /**
     * Applies a status after field validation and returns the affected project.
     *
     * @param context change status request context
     * @return the project after the status update
     * @throws ProjectNotFoundException when the project cannot be resolved
     */
    public Projects changeStatus(ProjectsChangeStatusContext context) throws ProjectNotFoundException {
        Projects project;
        try {
            project = projectByContext(context);
        } catch (RuntimeException ex) {
            throw new ProjectNotFoundException("Unable to resolve project for the status change request.", ex);
        }

        if (project == null || project.getId() == null) {
            throw new ProjectNotFoundException("Project identifier is missing in the status change request.");
        }

        String newStatusId = context.getNewStatus();
        Timestamp timeOfChange = context.getChangeDate() == null ? Timestamp.from(Instant.now())
                : Timestamp.from(context.getChangeDate());

        // Field validations
        // Status is not filled out
        if (newStatusId == null || newStatusId.isBlank()) {
            throw new ServiceException("status_missing");
        }

        // Same status selected
        if (newStatusId.equals(project.getStatusId())) {
            return project;
        }

        // Updating project
        createNewSnapshot(project.getId());
        projectRepository.updateProjectStatus(project.getId(), newStatusId, timeOfChange);

        // Add completed if status is final
        if (isStatusFinal(newStatusId)) {
            projectRepository.setCompletedTime(project.getId(), timeOfChange);
        }

        return project;
    }

    /**
     * Stores the current status as a snapshot before changing it.
     *
     * @param projectId project identifier
     */
    private void createNewSnapshot(String projectId) {
        String formerStatusId = getProjectStatusId(projectId);
        snapshotRepository.createSnapshot(formerStatusId, projectId);
    }

    /**
     * Loads a project by its identifier.
     *
     * @param projectId project identifier
     * @return project entity or {@code null} if missing
     */
    public Projects projectById(String projectId) {
        return projectRepository.projectById(projectId);
    }

    /**
     * Checks if the given status is marked as final.
     *
     * @param statusId status identifier
     * @return {@code true} when the status is final
     */
    private boolean isStatusFinal(String statusId) {
        return statusRepository.getFinalStatusByStatusId(statusId);
    }

    /**
     * Fetches the current status id for the given project.
     *
     * @param projectId project identifier
     * @return status identifier string
     */
    public String getProjectStatusId(String projectId) {
        return projectRepository.statusIdByProjectId(projectId);
    }

    /**
     * Marks the status field as read-only for active projects.
     *
     * @param projects list of projects from the read event
     */
    public void setStatusFieldToReadOnly(List<Projects> projects) {
        projects.forEach(p -> {
            if (p.getHasActiveEntity() != null && p.getHasActiveEntity()) {
                p.setStatusFieldAvailability(1);
            }
        });
    }
}
