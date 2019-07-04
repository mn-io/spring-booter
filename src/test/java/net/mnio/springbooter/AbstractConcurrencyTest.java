package net.mnio.springbooter;

import net.mnio.jConcurrencyOrchestra.InterruptService;
import net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@ActiveProfiles({"test", "concurrencyTest"})
public abstract class AbstractConcurrencyTest extends AbstractIntegrationTest {

    @Profile("concurrencyTest")
    @Configuration
    static class Config {

        @Bean
        InterruptService interruptService() {
            return new OrchestratedInterruptServiceImpl();
        }
    }
}
