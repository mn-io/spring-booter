package net.mnio.springbooter.persistence.repositories;

import net.mnio.springbooter.persistence.model.User;

import java.util.Optional;

public interface UserRepository extends AbstractRepository<User> {

    Optional<User> findById(final String id);

    Optional<User> findByEmail(final String email);
}
