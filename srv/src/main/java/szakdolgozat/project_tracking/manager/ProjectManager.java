package szakdolgozat.project_tracking.manager;

import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import org.springframework.stereotype.Component;
import szakdolgozat.project_tracking.repository.ProjectRepository;
import szakdolgozat.project_tracking.repository.SnapshotRepository;

import java.util.List;

@Component
public class ProjectManager {

    private final SnapshotRepository snapshotRepository;
    private final ProjectRepository projectRepository;


    public ProjectManager(SnapshotRepository snapshotRepository, ProjectRepository projectRepository) {
        this.snapshotRepository = snapshotRepository;
        this.projectRepository = projectRepository;
    }

    public void setStatusFieldToReadOnly(List<Projects> projects) {
        projects.forEach(p -> {
            if (p.getHasActiveEntity() != null && p.getHasActiveEntity()) {
                p.setStatusFieldAvailability(1);
            }
        });
    }
}
