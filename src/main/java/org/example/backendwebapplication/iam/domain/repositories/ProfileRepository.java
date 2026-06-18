package org.example.backendwebapplication.iam.domain.repositories;

import org.example.backendwebapplication.iam.domain.model.entities.Profile;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link Profile} entity persistence.
 * Implemented in the infrastructure layer.
 */
public interface ProfileRepository {

    Optional<Profile> findById(UUID profileId);

    Optional<Profile> findByUserId(UUID userId);

    Profile save(Profile profile);
}
