package org.example.backendwebapplication.iam.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST resource returned after successful authentication or registration.
 *
 * @param user         the authenticated/registered user
 * @param profile      the user's profile (only on registration)
 * @param accessToken  the JWT access token
 * @param refreshToken the opaque refresh token
 */
@Schema(description = "Response body for authentication and registration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponseResource(

        @Schema(description = "User information")
        UserResource user,

        @Schema(description = "Profile information (registration only)")
        ProfileResource profile,

        @Schema(description = "JWT access token (15-minute TTL)")
        String accessToken,

        @Schema(description = "Opaque refresh token (7-day TTL)")
        String refreshToken) {
}
