package org.example.backendwebapplication.iam.domain.repositories;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link User} aggregate persistence.
 * Implemented in the infrastructure layer.
 */
public interface UserRepository {

    Optional<User> findById(UUID userId);

    Optional<User> findByEmail(Email email);

    User save(User user);

    boolean existsByEmail(Email email);
}
