package org.example.backendwebapplication.iam.domain.model.commands;

/**
 * Command to update a user's profile information.
 *
 * @param accountId the ID of the account whose profile should be updated
 * @param fullName  the new full name
 * @param photoUrl  the new photo URL
 */
public record UpdateUserProfileCommand(Long accountId, String fullName, String photoUrl) {
}
