package net.mnio.springbooter.persistence.repositories;

import net.mnio.springbooter.AbstractIntegrationTest;
import net.mnio.springbooter.TestUtil;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Test
    public void dontCreateSameUserTwice() {
        final User user1 = TestUtil.createUser("email", "name", "pwd");
        final User user2 = TestUtil.createUser("email", "name", "pwd");

        final long userCountBefore = userRepository.count();
        userRepository.save(user1);

        assertEquals(userCountBefore + 1, userRepository.count());

        try {
            userRepository.save(user2);
            fail("Exception expected");
        } catch (Exception e) {
            final Throwable cause = e.getCause().getCause();
            assertEquals(java.sql.SQLIntegrityConstraintViolationException.class, cause.getClass());
            final String actualMessage = StringUtils.substringAfter(cause.getMessage(), " ");
            assertEquals("Duplicate entry 'email' for key 'UC_USER_EMAIL_COL'", actualMessage);
        }
        assertEquals(userCountBefore + 1, userRepository.count());
    }

    @Test
    public void noUserWithEmptyEmailAllowed() {
        final User user = TestUtil.createUser(null, "name", "pwd");

        final long userCountBefore = userRepository.count();

        try {
            userRepository.save(user);
            fail("Exception expected");
        } catch (Exception e) {
            final Throwable cause = e.getCause().getCause();
            assertEquals(java.sql.SQLIntegrityConstraintViolationException.class, cause.getClass());
            final String actualMessage = StringUtils.substringAfter(cause.getMessage(), " ");
            assertEquals("Column 'email' cannot be null", actualMessage);
        }
        assertEquals(userCountBefore, userRepository.count());
    }

    @Test
    public void noUserWithEmptyPasswordAllowed() {
        final User user = TestUtil.createUser("email", "name", null);

        final long userCountBefore = userRepository.count();

        try {
            userRepository.save(user);
            fail("Exception expected");
        } catch (Exception e) {
            final Throwable cause = e.getCause().getCause();
            assertEquals(java.sql.SQLIntegrityConstraintViolationException.class, cause.getClass());
            final String actualMessage = StringUtils.substringAfter(cause.getMessage(), " ");
            assertEquals("Column 'password' cannot be null", actualMessage);
        }
        assertEquals(userCountBefore, userRepository.count());
    }

    @Test
    public void onDeleteCascadeSessions() {
        final User user = TestUtil.createUser("email", "name", "pwd");
        final UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.generateToken();

        final long userCountBefore = userRepository.count();
        final long sessionCountBefore = userSessionRepository.count();

        final User savedUser = userRepository.save(user);

        assertEquals(userCountBefore + 1, userRepository.count());
        assertEquals(sessionCountBefore, userSessionRepository.count());

        userSessionRepository.save(userSession);

        assertEquals(sessionCountBefore + 1, userSessionRepository.count());

        userRepository.delete(savedUser);

        assertEquals(userCountBefore, userRepository.count());
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

}