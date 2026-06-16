package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.domain.repositories.ProfileRepository;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the domain {@link ProfileRepository} port
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository jpa;

    @Override
    public Optional<Profile> findById(UUID profileId) {
        return jpa.findByProfileId(profileId)
                .map(ProfilePersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Profile> findByUserId(UUID userId) {
        return jpa.findByUserId(userId)
                .map(ProfilePersistenceAssembler::toDomain);
    }

    @Override
    public Profile save(Profile profile) {
        var entity = ProfilePersistenceAssembler.toPersistence(profile);
        var saved = jpa.save(entity);
        return ProfilePersistenceAssembler.toDomain(saved);
    }
}
