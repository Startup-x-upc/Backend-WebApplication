package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * REST resource representing a User in API responses.
 * The password hash is NEVER included.
 *
 * @param id        the user's UUID
 * @param email     the user's email
 * @param role      the user's role
 * @param createdAt when the user was registered
 */
@Schema(description = "User information returned by the API")
public record UserResource(

        @Schema(description = "User UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "User's email address", example = "pasajero@correo.com")
        String email,

        @Schema(description = "User's role", example = "PASSENGER")
        String role,

        @Schema(description = "Registration timestamp")
        Instant createdAt) {
}
