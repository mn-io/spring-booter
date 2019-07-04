package net.mnio.springbooter.services.user;

import net.mnio.jConcurrencyOrchestra.InterruptService;
import net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl;
import net.mnio.jConcurrencyOrchestra.test.Task;
import net.mnio.springbooter.AbstractConcurrencyTest;
import net.mnio.springbooter.controller.api.UserCreateOrUpdateDto;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserServiceConcurrencyTest extends AbstractConcurrencyTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterruptService interruptService;

    @Test
    public void creatingSameUserConcurrent() throws Exception {
        final String email = "email";

        assertTrue(userRepository.findByEmail(email).isEmpty());
        final long userCountBefore = userRepository.count();

        final Task task1ToBeInterrupted = () -> {
            try {
                assertTrue(userRepository.findByEmail(email).isEmpty());
                final UserCreateOrUpdateDto dto = new UserCreateOrUpdateDto(email, "user1", "password");
                userService.createUser(dto); // here interruptService kicks in
                fail("Exception expected");
            } catch (Exception e) {
                final Throwable cause = e.getCause().getCause();
                assertEquals(java.sql.SQLIntegrityConstraintViolationException.class, cause.getClass());
                final String actualMessage = StringUtils.substringAfter(cause.getMessage(), " ");
                assertEquals("Duplicate entry 'email' for key 'UC_USER_EMAIL_COL'", actualMessage);
                assertTrue(userRepository.findByEmail(email).isPresent());
            }
        };

        final Task task2CreatingUserSuccessfully = () -> {
            assertTrue(userRepository.findByEmail(email).isEmpty());
            final UserCreateOrUpdateDto dto = new UserCreateOrUpdateDto(email, "user2", "password");
            final User user = userService.createUser(dto);
            assertNotNull(user.getId());
            assertEquals(user.getEmail(), dto.getEmail());
            assertTrue(userRepository.findByEmail(email).isPresent());
        };

        final boolean success = ((OrchestratedInterruptServiceImpl) interruptService)
                .start(
                        task1ToBeInterrupted,
                        task2CreatingUserSuccessfully,
                        task2CreatingUserSuccessfully
                );

        assertTrue(success);
        assertTrue(userRepository.findByEmail(email).isPresent());
        assertEquals(userCountBefore + 1, userRepository.count());
    }
}