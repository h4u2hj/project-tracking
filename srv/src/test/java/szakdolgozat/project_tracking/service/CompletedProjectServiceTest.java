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

import cds.gen.szakdolgozat.srv.service.completedprojectservice.CompletedProjectService;
import cds.gen.szakdolgozat.srv.service.completedprojectservice.CompletedProjectService_;
import cds.gen.szakdolgozat.srv.service.completedprojectservice.Projects;
import cds.gen.szakdolgozat.srv.service.completedprojectservice.Projects_;

@SpringBootTest
@ActiveProfiles("test")
class CompletedProjectServiceTest {

    private static final String COMPLETED_PROJECT_ID = "d4781331-fcef-443e-b6ad-b54616ec4d61";

    @Autowired
    @Qualifier(CompletedProjectService_.CDS_NAME)
    CompletedProjectService completedProjectService;
    
    @Test
    @WithMockUser(username = "admin", authorities = { "ProjectManager" })
    void completedProjectCanBeReadByAuthorizedUser() {
        Result result = completedProjectService
                .run(Select.from(Projects_.class).where(p -> p.ID().eq(COMPLETED_PROJECT_ID)));

        assertNotNull(result);

        Projects project = result.single(Projects.class);
        assertEquals(COMPLETED_PROJECT_ID, project.getId());
        assertEquals("New Reception", project.getName());
    }

    @Test
    @WithMockUser(username = "viewer")
    void completedProjectReadRejectedForUnauthorizedUser() {
        assertThrows(ContextualizedServiceException.class, () -> completedProjectService
                .run(Select.from(Projects_.class).where(p -> p.ID().eq(COMPLETED_PROJECT_ID))));
    }
}
