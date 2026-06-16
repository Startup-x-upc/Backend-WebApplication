package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.model.valueobjects.PasswordHash;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

/**
 * Stateless assembler that translates between the {@link User} domain
 * aggregate and its {@link UserPersistenceEntity} JPA representation.
 */
public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {}

    /**
     * Converts a JPA entity to a domain aggregate.
     */
    public static User toDomain(UserPersistenceEntity entity) {
        return new User(
                entity.getUserId(),
                new Email(entity.getEmail()),
                PasswordHash.fromHash(entity.getPasswordHash()),
                entity.getRole());
    }

    /**
     * Converts a domain aggregate to a JPA entity.
     */
    public static UserPersistenceEntity toPersistence(User user) {
        var entity = new UserPersistenceEntity();
        entity.setUserId(user.getUserId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRole(user.getRole());
        return entity;
    }
}
