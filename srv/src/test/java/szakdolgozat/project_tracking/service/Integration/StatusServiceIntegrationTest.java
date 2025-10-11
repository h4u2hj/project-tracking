package szakdolgozat.project_tracking.service.Integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StatusServiceIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void types_are_exposed_via_odata() throws Exception {
        mockMvc.perform(get("/odata/v4/StatusService/Status")     // call the OData endpoint
                        .with(httpBasic("admin", "admin")))    // use mock credentials
                .andExpect(status().isOk())               // HTTP 200
                .andExpect(jsonPath("$.value").exists()); // CAP wraps results in {"value":[ ... ]}
    }
}
