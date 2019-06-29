package net.mnio.springbooter.services.user;

import net.mnio.springbooter.AbstractUnitTest;
import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserSessionServiceUnitTest extends AbstractUnitTest {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserSessionService userSessionService;

    @Test
    public void createSessionHappyPath() {
        final User user = new User();
        final UserSession session = userSessionService.createSession(user);

        verify(userSessionRepository, times(1)).save(any(UserSession.class));
        assertTrue(StringUtils.isNotBlank(session.getToken()));
        assertEquals(session.getUser(), user);
    }

    @Test
    public void createSessionsWithDifferentTokens() {
        final User user = new User();
        final UserSession session1 = userSessionService.createSession(user);
        final UserSession session2 = userSessionService.createSession(user);

        verify(userSessionRepository, times(2)).save(any(UserSession.class));
        assertNotEquals(session1.getToken(), session2.getToken());
        assertEquals(session1.getUser(), session2.getUser());
    }
}