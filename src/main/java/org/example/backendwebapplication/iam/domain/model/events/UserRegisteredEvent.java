package org.example.backendwebapplication.iam.domain.model.events;

import java.util.UUID;

/**
 * Integration event emitted when a new User is successfully registered.
 * <p>Currently no external consumers, but published for audit and future
 * bounded contexts (e.g. notifications).</p>
 *
 * @param userId   the newly created user's UUID
 * @param email    the user's email
 * @param role     the user's role (PASSENGER, DRIVER, ADMIN)
 * @param fullName the user's display name
 */
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String role,
        String fullName) {
}
