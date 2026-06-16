package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST resource returned after a successful token refresh.
 *
 * @param accessToken  the new JWT access token
 * @param refreshToken the new opaque refresh token
 */
@Schema(description = "Response body for token refresh")
public record TokenRefreshResponse(

        @Schema(description = "New JWT access token (15-minute TTL)")
        String accessToken,

        @Schema(description = "New opaque refresh token (7-day TTL)")
        String refreshToken) {
}
