package szakdolgozat.project_tracking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.services.impl.ContextualizedServiceException;

import cds.gen.szakdolgozat.srv.service.projectservice.ProjectService;
import cds.gen.szakdolgozat.srv.service.projectservice.ProjectService_;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects;
import cds.gen.szakdolgozat.srv.service.projectservice.Projects_;

@SpringBootTest
@ActiveProfiles("test")
class ProjectServiceTest {

    private static final String PROJECT_ID = "3506561e-ad35-4d0e-905f-7fe5f85bdda1";

    @Autowired
    @Qualifier(ProjectService_.CDS_NAME)
    ProjectService projectService;

    /**
     * Ensures an authorized user can retrieve a project by ID.
     */
    @Test
    @WithMockUser(username = "admin", authorities = { "ProjectManager" })
    void projectCanBeReadByAuthorizedUser() {
        Result result = projectService.run(Select.from(Projects_.class).where(p -> p.ID().eq(PROJECT_ID)));

        assertNotNull(result);

        Projects project = result.single(Projects.class);
        assertEquals(PROJECT_ID, project.getId());
        assertEquals("New Website", project.getName());
    }

    /**
     * Verifies project reads are rejected for unauthorized users.
     */
    @Test
    @WithMockUser(username = "viewer")
    void projectReadRejectedForUnauthorizedUser() {
        assertThrows(ContextualizedServiceException.class,
                () -> projectService.run(Select.from(Projects_.class).where(p -> p.ID().eq(PROJECT_ID))));
    }
}
