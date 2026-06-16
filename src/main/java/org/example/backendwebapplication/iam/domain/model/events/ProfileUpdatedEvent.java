package org.example.backendwebapplication.iam.domain.model.events;

import java.util.UUID;

/**
 * Integration event emitted when a user profile is updated.
 * <p>Consumed by:</p>
 * <ul>
 *   <li><b>Driver Management</b> — if the user is a driver, syncs
 *       fullName and photoUrl to the Driver entity</li>
 * </ul>
 *
 * @param userId    the user's UUID
 * @param profileId the profile's UUID
 * @param fullName  the updated full name
 * @param photoUrl  the updated photo URL
 */
public record ProfileUpdatedEvent(
        UUID userId,
        UUID profileId,
        String fullName,
        String photoUrl) {
}
