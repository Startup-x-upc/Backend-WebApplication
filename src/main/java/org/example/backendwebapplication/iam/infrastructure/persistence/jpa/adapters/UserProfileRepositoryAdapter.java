package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;
import org.example.backendwebapplication.iam.domain.repositories.UserProfileRepository;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers.UserProfilePersistenceAssembler;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements the domain {@link UserProfileRepository} port
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements UserProfileRepository {

    private final UserProfileJpaRepository jpa;

    @Override
    public Optional<UserProfile> findById(Long id) {
        return jpa.findById(id)
                .map(UserProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<UserProfile> findByAccountId(Long accountId) {
        return jpa.findByAccountId(accountId)
                .map(UserProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<UserProfile> findAll() {
        return jpa.findAll().stream()
                .map(UserProfilePersistenceAssembler::toDomainFromPersistence)
                .collect(Collectors.toList());
    }

    @Override
    public UserProfile save(UserProfile profile) {
        var entity = UserProfilePersistenceAssembler.toPersistenceFromDomain(profile);
        var saved  = jpa.save(entity);
        return UserProfilePersistenceAssembler.toDomainFromPersistence(saved);
    }
}
