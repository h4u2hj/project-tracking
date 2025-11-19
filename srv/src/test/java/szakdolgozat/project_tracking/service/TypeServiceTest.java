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

import cds.gen.szakdolgozat.srv.service.typeservice.Type;
import cds.gen.szakdolgozat.srv.service.typeservice.TypeService;
import cds.gen.szakdolgozat.srv.service.typeservice.TypeService_;
import cds.gen.szakdolgozat.srv.service.typeservice.Type_;

@SpringBootTest
@ActiveProfiles("test")
class TypeServiceTest {

    private static final String TYPE_ID = "22756613-bcde-463b-a379-ff65f07d072f";

    @Autowired
    @Qualifier(TypeService_.CDS_NAME)
    TypeService typeService;

    @Test
    @WithMockUser(username = "admin", authorities = { "Administrator", "ProjectManager" })
    void typeCanBeReadByAuthorizedUser() {
        Result result = typeService.run(Select.from(Type_.class).where(t -> t.ID().eq(TYPE_ID)));

        assertNotNull(result);

        Type type = result.single(Type.class);
        assertEquals(TYPE_ID, type.getId());
        assertEquals("Application/software", type.getName());
    }

    @Test
    @WithMockUser(username = "viewer")
    void typeReadRejectedForUnauthorizedUser() {
        assertThrows(ContextualizedServiceException.class,
                () -> typeService.run(Select.from(Type_.class).where(t -> t.ID().eq(TYPE_ID))));
    }
}