package net.mnio.springbooter;

import net.mnio.jOrchestra.InterruptService;
import net.mnio.jOrchestra.test.OrchestratedInterruptServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@ActiveProfiles({"test", "orchestrationTest"})
public abstract class AbstractOrchestrationTest extends AbstractIntegrationTest {

    @Profile("orchestrationTest")
    @Configuration
    static class Config {

        @Bean
        InterruptService interruptService() {
            return new OrchestratedInterruptServiceImpl();
        }
    }

}
