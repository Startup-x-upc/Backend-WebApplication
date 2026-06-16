package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST resource for passenger registration requests.
 *
 * @param email    valid email address
 * @param password minimum 6 characters
 * @param fullName display name (2–100 characters)
 */
@Schema(description = "Request body for passenger registration")
public record RegisterPassengerResource(

        @Email
        @NotBlank
        @Schema(description = "User's email address", example = "pasajero@correo.com")
        String email,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "User's password (min 6 characters)", example = "pass123")
        String password,

        @NotBlank
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        @Schema(description = "User's full display name", example = "María Quispe")
        String fullName) {
}
