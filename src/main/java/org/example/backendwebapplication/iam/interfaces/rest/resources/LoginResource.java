package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * REST resource for login requests.
 *
 * @param email    the user's registered email
 * @param password the user's raw password
 */
@Schema(description = "Request body for login")
public record LoginResource(

        @Email
        @NotBlank
        @Schema(description = "User's email address", example = "conductor@correo.com")
        String email,

        @NotBlank
        @Schema(description = "User's password", example = "pass123")
        String password) {
}
