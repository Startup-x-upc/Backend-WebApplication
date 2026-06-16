package org.example.backendwebapplication.iam.interfaces.acl;

import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;

import java.util.Optional;
import java.util.UUID;

/**
 * Anti-Corruption Layer (ACL) facade for the IAM bounded context.
 * <p>Exposes a minimal, stable contract for other bounded contexts
 * (Driver Management, Monetization, etc.) to query identity data
 * without coupling to IAM's internal domain model.</p>
 *
 * <p>Implementation lives in {@code application.acl}.</p>
 */
public interface IamContextFacade {

    /**
     * Checks whether a user with the given UUID exists.
     *
     * @param userId the user's business identifier
     * @return {@code true} if the user exists
     */
    boolean existsUserById(UUID userId);

    /**
     * Returns the role of a user by their UUID.
     *
     * @param userId the user's business identifier
     * @return the UserRole, or {@code Optional.empty()} if not found
     */
    Optional<UserRole> getUserRoleById(UUID userId);

    /**
     * Returns the full display name of a user by their UUID.
     * <p>Reads from the Profile entity associated with the user.</p>
     *
     * @param userId the user's business identifier
     * @return the full name, or {@code Optional.empty()} if no profile exists
     */
    Optional<String> getFullNameByUserId(UUID userId);
}
