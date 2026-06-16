package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.repositories.UserRepository;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the domain {@link UserRepository} port
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository jpa;

    @Override
    public Optional<User> findById(UUID userId) {
        return jpa.findByUserId(userId)
                .map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.address())
                .map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = UserPersistenceAssembler.toPersistence(user);
        var saved = jpa.save(entity);
        return UserPersistenceAssembler.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.address());
    }
}
