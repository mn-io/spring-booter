package net.mnio.springbooter.persistence.repositories;

import net.mnio.springbooter.AbstractIntegrationTest;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import static net.mnio.springbooter.TestUtil.createUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserSessionRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Test
    public void dontCreateSessionWithEmptyToken() {
        final User user = createUser("email", "name", "pwd");
        final UserSession userSession = new UserSession();
        userSession.setUser(user);

        final long sessionCountBefore = userSessionRepository.count();

        userRepository.save(user);
        try {
            userSessionRepository.save(userSession);
            fail("Exception expected");
        } catch (Exception e) {
            final Throwable cause = e.getCause().getCause();
            assertEquals(java.sql.SQLIntegrityConstraintViolationException.class, cause.getClass());
            final String actualMessage = StringUtils.substringAfter(cause.getMessage(), " ");
            assertEquals("Column 'token' cannot be null", actualMessage);
        }
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

    @Test
    public void dontCreateSessionWithoutUser() {
        final UserSession userSession = new UserSession();
        userSession.generateToken();

        final long sessionCountBefore = userSessionRepository.count();

        try {
            userSessionRepository.save(userSession);
            fail("Exception expected");
        } catch (Exception e) {
            final Throwable cause = e.getCause().getCause();
            assertEquals(java.sql.SQLIntegrityConstraintViolationException.class, cause.getClass());
            final String actualMessage = StringUtils.substringAfter(cause.getMessage(), " ");
            assertEquals("Column 'user_id' cannot be null", actualMessage);
        }
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

    @Test
    public void dontCreateSessionWithSameToken() {
        final User user = createUser("email", "name", "pwd");
        final UserSession userSession1 = new UserSession();
        userSession1.setUser(user);
        overwriteToken(userSession1, "hello");
        final UserSession userSession2 = new UserSession();
        userSession2.setUser(user);
        overwriteToken(userSession2, "hello");

        userRepository.save(user);
        userSessionRepository.save(userSession1);

        try {
            userSessionRepository.save(userSession2);
            fail("Exception expected");
        } catch (Exception e) {
            final Throwable cause = e.getCause().getCause();
            assertEquals(java.sql.SQLIntegrityConstraintViolationException.class, cause.getClass());
            final String actualMessage = StringUtils.substringAfter(cause.getMessage(), " ");
            assertEquals("Duplicate entry 'hello' for key 'UC_USER_SESSION_TOKEN_COL'", actualMessage);
        }
    }

    @Test
    public void dontDeleteUserWhenSessionIsDeleted() {
        final User user = createUser("email", "name", "pwd");
        final UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.generateToken();

        final long userCountBefore = userRepository.count();
        final long sessionCountBefore = userSessionRepository.count();

        userRepository.save(user);
        userSessionRepository.save(userSession);

        assertEquals(userCountBefore + 1, userRepository.count());
        assertEquals(sessionCountBefore + 1, userSessionRepository.count());

        userSessionRepository.delete(userSession);
        assertEquals(userCountBefore + 1, userRepository.count());
        assertEquals(sessionCountBefore, userSessionRepository.count());
    }

    private void overwriteToken(final UserSession userSession, final String value) {
        ReflectionUtils.doWithFields(UserSession.class, field -> {
            if (field.getName().equals("token")) {
                ReflectionUtils.makeAccessible(field);
                field.set(userSession, value);
            }
        });
    }
}