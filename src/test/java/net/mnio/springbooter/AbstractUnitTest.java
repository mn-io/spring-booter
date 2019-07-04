package net.mnio.springbooter;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mnio.jConcurrencyOrchestra.InterruptService;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
//https://stackoverflow.com/questions/46343782/whats-the-difference-between-autoconfigurewebmvc-and-autoconfiguremockmvc
@AutoConfigureMockMvc

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")

@ContextConfiguration
// extra component scan needed because of context configuration
@ComponentScan(basePackages = {"net.mnio.springbooter"})
public abstract class AbstractUnitTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
    static class Config {

        @Bean
        UserSessionRepository userSessionRepository() {
            final UserSessionRepository mock = mock(UserSessionRepository.class);
            when(mock.save(any(UserSession.class))).thenAnswer(returnsFirstArg());
            return mock;
        }

        @Bean
        UserRepository userRepository() {
            final UserRepository mock = mock(UserRepository.class);
            when(mock.save(any(User.class))).thenAnswer(returnsFirstArg());
            return mock;
        }

        @Bean
        InterruptService interruptService() {
            return new InterruptService() {
            };
        }

        @Bean
        PlatformTransactionManager platformTransactionManager() {
            return mock(PlatformTransactionManager.class);
        }
    }

    @Autowired
    protected MockMvc mvc;

    protected final ObjectMapper mapper = new ObjectMapper();
}
