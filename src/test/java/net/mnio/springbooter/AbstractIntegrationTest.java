package net.mnio.springbooter;

import net.mnio.springbooter.bootstrap.EnvironmentProvider;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)

//https://stackoverflow.com/questions/46343782/whats-the-difference-between-autoconfigurewebmvc-and-autoconfiguremockmvc
@AutoConfigureMockMvc

//Runs liquibase update before each test, see application-test.yml
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private EnvironmentProvider environmentProvider;

//    protected final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        final String dbInfo = environmentProvider.getDatasourceUrl();
        if (!dbInfo.endsWith("_test")) {
            throw new IllegalStateException("Tests must use db with suffix '_test'");
        }
    }
}
