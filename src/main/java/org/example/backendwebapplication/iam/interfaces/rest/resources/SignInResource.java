package org.example.backendwebapplication.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * REST resource for authentication requests.
 *
 * @param email    the user's registered email
 * @param password the user's raw password
 */
public record SignInResource(
        @Email @NotBlank String email,
        @NotBlank String password) {
}
