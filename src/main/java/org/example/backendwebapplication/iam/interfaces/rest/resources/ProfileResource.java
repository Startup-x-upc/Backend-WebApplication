package org.example.backendwebapplication.iam.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * REST resource representing a Profile in API responses.
 * <p>For the {@code GET /users/me/profile} read model, {@code email} and
 * {@code role} are composed from the associated User aggregate.</p>
 *
 * @param id        the profile UUID
 * @param userId    the associated user UUID
 * @param email     the user's email (composed from User)
 * @param fullName  the display name
 * @param role      the user's role (composed from User)
 * @param photoUrl  the profile photo URL
 * @param createdAt when the profile was created
 * @param updatedAt when the profile was last updated
 */
@Schema(description = "Profile information returned by the API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileResource(

        @Schema(description = "Profile UUID", example = "660e8400-e29b-41d4-a716-446655440001")
        UUID id,

        @Schema(description = "Associated user UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "User's email (composed from User)", example = "pasajero@correo.com")
        String email,

        @Schema(description = "Display name", example = "María Quispe")
        String fullName,

        @Schema(description = "User's role (composed from User)", example = "PASSENGER")
        String role,

        @Schema(description = "Profile photo URL", example = "https://i.pravatar.cc/150?img=47")
        String photoUrl,

        @Schema(description = "Creation timestamp")
        Instant createdAt,

        @Schema(description = "Last update timestamp")
        Instant updatedAt) {
}
