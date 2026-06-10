package org.example.backendwebapplication.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * REST resource for profile update requests.
 *
 * @param fullName new display name
 * @param photoUrl new photo URL (optional)
 */
public record UpdateUserProfileResource(
        @NotBlank String fullName,
        String photoUrl) {
}
