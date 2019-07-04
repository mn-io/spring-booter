package net.mnio.springbooter.bootstrap.session;

import net.mnio.springbooter.persistence.model.UserSession;
import net.mnio.springbooter.persistence.repositories.UserSessionRepository;

import java.util.Optional;

public final class UserSessionContext {

    private static final ThreadLocal<UserSession> instance = new ThreadLocal<>();

    private UserSessionContext() {
    }

    public static Optional<UserSession> buildInstance(final UserSessionRepository sessionRepository, final String authToken) {
        if (instance.get() != null) {
            throw new IllegalStateException("Illegal User Session found.");
        }

        final Optional<UserSession> restoredSession = sessionRepository.findByToken(authToken);
        if (restoredSession.isPresent()) {
            final UserSession tmp = restoredSession.get();
            //TODO: auto logout - update timestamp
            instance.set(tmp);
            return restoredSession;
        } else {
            return Optional.empty();
        }
    }

    public static UserSession getSession() {
        return instance.get();
    }

    public static void clear() {
        instance.remove();
    }
}