package net.mnio.springbooter.persistence.repositories;

import net.mnio.springbooter.persistence.model.UserSession;

import java.util.Optional;

public interface UserSessionRepository extends AbstractRepository<UserSession> {

    Optional<UserSession> findByToken(final String token);
}
