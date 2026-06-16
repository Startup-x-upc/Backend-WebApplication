package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.repositories.UserRepository;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the domain {@link UserRepository} port
 * using Spring Data JPA.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository jpa;
    private final ApplicationEventPublisher eventPublisher;

    public UserRepositoryImpl(UserPersistenceRepository jpa,
                              ApplicationEventPublisher eventPublisher) {
        this.jpa = jpa;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return jpa.findByUserId(userId.toString())
                .map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.address())
                .map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public User save(User user) {
        // Look up the existing entity to preserve the internal Long id
        // so JPA issues an UPDATE instead of an INSERT
        var existing = jpa.findByUserId(user.getUserId().toString())
                .orElse(null);

        var entity = UserPersistenceAssembler.toPersistence(user);

        if (existing != null) {
            entity.setId(existing.getId());
        }

        var saved = jpa.save(entity);

        user.domainEvents().forEach(eventPublisher::publishEvent);
        user.clearDomainEvents();

        return UserPersistenceAssembler.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.address());
    }
}
