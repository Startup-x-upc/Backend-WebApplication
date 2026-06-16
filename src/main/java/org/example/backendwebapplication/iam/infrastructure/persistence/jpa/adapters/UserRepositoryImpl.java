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
 * <p>Explicitly publishes domain events registered on the {@link User}
 * aggregate after persistence, since the JPA repository operates on
 * {@code UserPersistenceEntity} (not the aggregate itself) and Spring Data's
 * {@code @DomainEvents} mechanism would never fire otherwise.</p>
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

        // Publish domain events registered on the aggregate.
        // Must be done explicitly because jpa.save() operates on the
        // persistence entity, not the domain aggregate root.
        user.domainEvents().forEach(eventPublisher::publishEvent);
        user.clearDomainEvents();

        return UserPersistenceAssembler.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.address());
    }
}
