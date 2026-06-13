package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;
import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.UserProfilePersistenceEntity;

/**
 * Assembler that translates between the {@link UserProfile} domain aggregate
 * and its {@link UserProfilePersistenceEntity} JPA representation.
 */
public final class UserProfilePersistenceAssembler {

    private UserProfilePersistenceAssembler() {}

    /**
     * Converts a JPA entity to a domain aggregate.
     */
    public static UserProfile toDomainFromPersistence(UserProfilePersistenceEntity entity) {
        return new UserProfile(
                entity.getId(),
                entity.getAccountId(),
                entity.getFullName(),
                entity.getEmail().address(),
                entity.getPhotoUrl());
    }

    /**
     * Converts a domain aggregate to a JPA entity.
     */
    public static UserProfilePersistenceEntity toPersistenceFromDomain(UserProfile profile) {
        var entity = new UserProfilePersistenceEntity();
        entity.setId(profile.getId());
        entity.setAccountId(profile.getAccountId());
        entity.setFullName(profile.getFullName());
        entity.setEmail(new EmailAddress(profile.getEmail()));
        entity.setPhotoUrl(profile.getPhotoUrl());
        return entity;
    }
}
