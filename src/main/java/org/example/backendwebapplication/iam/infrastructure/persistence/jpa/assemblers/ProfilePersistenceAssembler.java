package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.domain.model.valueobjects.FullName;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;

/**
 * Stateless assembler that translates between the {@link Profile} domain
 * entity and its {@link ProfilePersistenceEntity} JPA representation.
 */
public final class ProfilePersistenceAssembler {

    private ProfilePersistenceAssembler() {}

    /**
     * Converts a JPA entity to a domain entity.
     */
    public static Profile toDomain(ProfilePersistenceEntity entity) {
        return new Profile(
                entity.getProfileId(),
                entity.getUserId(),
                new FullName(entity.getFullName()),
                entity.getPhotoUrl());
    }

    /**
     * Converts a domain entity to a JPA entity.
     */
    public static ProfilePersistenceEntity toPersistence(Profile profile) {
        var entity = new ProfilePersistenceEntity();
        entity.setProfileId(profile.getProfileId());
        entity.setUserId(profile.getUserId());
        entity.setFullName(profile.getFullName());
        entity.setPhotoUrl(profile.getPhotoUrl());
        return entity;
    }
}
