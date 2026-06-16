package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link ProfilePersistenceEntity}.
 */
public interface ProfilePersistenceRepository extends JpaRepository<ProfilePersistenceEntity, Long> {

    Optional<ProfilePersistenceEntity> findByUserId(UUID userId);

    Optional<ProfilePersistenceEntity> findByProfileId(UUID profileId);
}
