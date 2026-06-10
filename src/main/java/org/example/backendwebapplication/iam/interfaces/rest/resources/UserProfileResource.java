package org.example.backendwebapplication.iam.interfaces.rest.resources;

/**
 * REST resource representing a user profile.
 *
 * @param id        the profile identifier
 * @param accountId the associated account identifier
 * @param fullName  the user's display name
 * @param email     the user's email address
 * @param photoUrl  the user's profile photo URL
 */
public record UserProfileResource(
        Long id,
        Long accountId,
        String fullName,
        String email,
        String photoUrl) {
}
