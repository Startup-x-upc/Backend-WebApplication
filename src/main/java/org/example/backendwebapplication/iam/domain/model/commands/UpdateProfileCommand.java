package org.example.backendwebapplication.iam.domain.model.commands;

import java.util.UUID;

/**
 * Command to update a user's profile information.
 *
 * @param userId   the ID of the user whose profile should be updated
 * @param fullName the new full name
 * @param photoUrl the new photo URL (optional)
 */
public record UpdateProfileCommand(
        UUID userId,
        String fullName,
        String photoUrl) {
}
