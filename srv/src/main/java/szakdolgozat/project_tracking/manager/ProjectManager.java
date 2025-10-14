package szakdolgozat.project_tracking.manager;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectsChangeStatusContext;
import com.sap.cds.services.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.repository.ProjectRepository;
import szakdolgozat.project_tracking.repository.SnapshotRepository;
import szakdolgozat.project_tracking.repository.StatusRepository;
import szakdolgozat.project_tracking.utilities.EventContextAnalyzer;
import szakdolgozat.project_tracking.utilities.ProjectNotFoundException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class ProjectManager {

    private final SnapshotRepository snapshotRepository;
    private final ProjectRepository projectRepository;
    private final StatusRepository statusRepository;

    @Autowired
    private EventContextAnalyzer eventContextAnalyzer;

    public ProjectManager(SnapshotRepository snapshotRepository, ProjectRepository projectRepository, StatusRepository statusRepository) {
        this.snapshotRepository = snapshotRepository;
        this.projectRepository = projectRepository;
        this.statusRepository = statusRepository;
    }

    public Projects projectByContext(ProjectsChangeStatusContext context) {
        var targetKeys = eventContextAnalyzer.targetKeys(context, ctx -> ctx.getCqn().ref());
        var projectId = targetKeys.get(Projects.ID);
        if (projectId == null) {
            return null;
        }

        return projectById(projectId.toString());
    }

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

        //Field validations
        //Status is not filled out
        if (newStatusId == null || newStatusId.isBlank()) {
            throw new ServiceException("status_missing");
        }

        //Same status selected
        if (newStatusId.equals(project.getStatusId())) {
            return project;
        }

        //Updating project
        createNewSnapshot(project.getId());
        projectRepository.updateProjectStatus(project.getId(), newStatusId, timeOfChange);

        //Add completed if status is final
        if (isStatusFinal(newStatusId)) {
            projectRepository.setCompletedTime(project.getId(), timeOfChange);
        }

        return project;
    }

    private void createNewSnapshot(String projectId) {
        String formerStatusId = getProjectStatusId(projectId);
        snapshotRepository.createSnapshot(formerStatusId, projectId);
    }

    public Projects projectById(String projectId) {
        return projectRepository.projectById(projectId);
    }

    private boolean isStatusFinal(String statusId) {
        return statusRepository.getFinalStatusByStatusId(statusId);
    }

    public String getProjectStatusId(String projectId) {
        return projectRepository.statusIdByProjectId(projectId);
    }

    public void setStatusFieldToReadOnly(List<Projects> projects) {
        projects.forEach(p -> {
            if (p.getHasActiveEntity() != null && p.getHasActiveEntity()) {
                p.setStatusFieldAvailability(1);
            }
        });
    }
}
