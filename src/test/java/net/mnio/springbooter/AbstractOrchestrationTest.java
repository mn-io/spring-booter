package net.mnio.springbooter;

import net.mnio.jOrchestra.InterruptService;
import net.mnio.jOrchestra.test.OrchestratedInterruptServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class AbstractOrchestrationTest extends AbstractIntegrationTest {

    @Configuration
    static class Config {

        @Bean
        InterruptService interruptService() {
            return new OrchestratedInterruptServiceImpl();
        }
    }

}
