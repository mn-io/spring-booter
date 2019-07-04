package net.mnio.springbooter.services.user;

import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;

@Service
public class UserSessionService {

    @Log
    private Logger log;

    @Autowired
    private UserSessionRepository userSessionRepository;

    public UserSession createSession(final User user) {
        final UserSession session = new UserSession();
        session.setUser(user);
        session.generateToken();

        final UserSession saved = userSessionRepository.save(session);
        log.debug("Session created for user {}", user.getEmail());
        return saved;
    }

    //TODO: auto logout - tests whether retry works on updating at the same time
    @Retryable(OptimisticLockException.class)
    public void destroySession(final UserSession session) {
        final String email = session.getUser().getEmail();
        userSessionRepository.delete(session);
        log.debug("Session destroyed for user {}", email);
    }
}
