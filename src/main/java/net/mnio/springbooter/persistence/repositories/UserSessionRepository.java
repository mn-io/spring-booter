package net.mnio.springbooter.persistence.repositories;

import net.mnio.springbooter.persistence.model.UserSession;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends AbstractRepository<UserSession> {

    Optional<UserSession> findByToken(final String token);

    List<UserSession> findAllByUserId(final String id);

    List<UserSession> findAllByOrderByLastModified();
}
