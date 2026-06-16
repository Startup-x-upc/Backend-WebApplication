package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Composed read model for {@code GET /users/me/profile}.
 * <p>Merges Profile display data with User's email and role so the
 * frontend doesn't need to make two calls.</p>
 *
 * @param id        the profile UUID
 * @param userId    the associated user UUID
 * @param email     the user's email (from User)
 * @param fullName  the display name
 * @param role      the user's role (from User)
 * @param photoUrl  the profile photo URL
 * @param createdAt when the profile was created
 */
@Schema(description = "Composed profile + user read model (GET /users/me/profile)")
public record MyProfileResource(

        @Schema(description = "Profile UUID", example = "660e8400-e29b-41d4-a716-446655440001")
        UUID id,

        @Schema(description = "Associated user UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "User's email address", example = "pasajero@correo.com")
        String email,

        @Schema(description = "Display name", example = "María Quispe")
        String fullName,

        @Schema(description = "User's role", example = "PASSENGER")
        String role,

        @Schema(description = "Profile photo URL", example = "https://i.pravatar.cc/150?img=47")
        String photoUrl,

        @Schema(description = "Creation timestamp")
        Instant createdAt) {
}
