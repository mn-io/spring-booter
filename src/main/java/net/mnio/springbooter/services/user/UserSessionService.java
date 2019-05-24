package net.mnio.springbooter.services.user;

import net.mnio.springbooter.persistence.model.User;
import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;

@Service
public class UserSessionService {

    @Log
    private Logger log;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Transactional
    public UserSession createSession(final User user) {
        final UserSession session = new UserSession();
        session.setUser(user);
        session.generateToken();
        return userSessionRepository.save(session);
    }

    @Async
    @Retryable(OptimisticLockException.class)
    public void destroySession(final String sessionId) {
        userSessionRepository.deleteById(sessionId);
    }
}
