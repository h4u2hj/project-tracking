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

import cds.gen.szakdolgozat.srv.service.statusservice.Status;
import cds.gen.szakdolgozat.srv.service.statusservice.StatusService;
import cds.gen.szakdolgozat.srv.service.statusservice.StatusService_;
import cds.gen.szakdolgozat.srv.service.statusservice.Status_;

@SpringBootTest
@ActiveProfiles("test")
class StatusServiceTest {

    private static final String STATUS_ID = "65891071-3d68-42ff-981b-988f6a2bc33b";

    @Autowired
    @Qualifier(StatusService_.CDS_NAME)
    private StatusService statusService;

    /**
     * Confirms an authorized user can fetch a status by its identifier.
     */
    @Test
    @WithMockUser(username = "admin", authorities = { "Administrator", "ProjectManager" })
    void testSelectStatusById() {

        Result result = statusService.run(Select.from(Status_.class).where(x -> x.ID().eq(STATUS_ID)));

        assertNotNull(result, "Service should return a result");

        Status status = result.single(Status.class);
        assertEquals(STATUS_ID, status.getId());
        assertEquals("In development", status.getName());
    }

    /**
     * Verifies unauthorized users are blocked from reading statuses.
     */
    @Test
    @WithMockUser(username = "viewer")
    void testSelectStatusByIdUnauthorized() {
        assertThrows(ContextualizedServiceException.class,
                () -> statusService.run(Select.from(Status_.class).where(x -> x.ID().eq(STATUS_ID))));
    }
}
