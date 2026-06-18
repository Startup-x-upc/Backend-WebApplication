package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.model.valueobjects.PasswordHash;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * Stateless assembler that translates between the {@link User} domain
 * aggregate and its {@link UserPersistenceEntity} JPA representation.
 */
public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {}

    public static User toDomain(UserPersistenceEntity entity) {
        return new User(
                UUID.fromString(entity.getUserId()),
                new Email(entity.getEmail()),
                PasswordHash.fromHash(entity.getPasswordHash()),
                entity.getRole(),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    public static UserPersistenceEntity toPersistence(User user) {
        var entity = new UserPersistenceEntity();
        entity.setUserId(user.getUserId().toString());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRole(user.getRole());
        return entity;
    }

    private static Instant toInstant(java.util.Date date) {
        return date != null ? date.toInstant() : null;
    }
}
