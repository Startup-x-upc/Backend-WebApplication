package org.example.backendwebapplication.iam.domain.model.commands;

import java.util.UUID;

/**
 * Command to update a user's profile information.
 *
 * @param profileId       the profile UUID (from URL path)
 * @param requesterUserId the UUID of the authenticated user making the request
 * @param fullName        the new full name
 * @param photoUrl        the new photo URL (optional)
 */
public record UpdateProfileCommand(
        UUID profileId,
        UUID requesterUserId,
        String fullName,
        String photoUrl) {
}
