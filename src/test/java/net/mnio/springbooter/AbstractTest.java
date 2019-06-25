package net.mnio.springbooter;

import net.mnio.jOrchestra.InterruptService;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
//https://stackoverflow.com/questions/46343782/whats-the-difference-between-autoconfigurewebmvc-and-autoconfiguremockmvc
@AutoConfigureMockMvc

@ActiveProfiles("test")

@ContextConfiguration
// extra component scan needed because of context configuration
@ComponentScan(basePackages = {"net.mnio.springbooter"})
public abstract class AbstractTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
    static class Config {

        @Bean
        UserSessionRepository userSessionRespRepository() {
            return mock(UserSessionRepository.class);
        }

        @Bean
        UserRepository userRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        InterruptService interruptService() {
            return new InterruptService() {
            };
        }
    }

    @Autowired
    protected MockMvc mvc;
}
