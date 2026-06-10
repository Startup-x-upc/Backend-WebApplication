package org.example.backendwebapplication.iam.interfaces.rest.resources;

import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * REST resource for account registration requests.
 *
 * @param email    valid email address
 * @param password minimum 4 characters
 * @param role     PASSENGER or DRIVER
 * @param fullName display name
 * @param photoUrl optional profile photo URL
 */
public record SignUpResource(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 4, message = "Password must be at least 4 characters") String password,
        @NotNull UserRole role,
        @NotBlank String fullName,
        String photoUrl) {
}
