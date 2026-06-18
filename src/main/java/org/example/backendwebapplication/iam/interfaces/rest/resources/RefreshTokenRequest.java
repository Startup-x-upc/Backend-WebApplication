package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * REST resource for refresh token requests.
 *
 * @param refreshToken the opaque refresh token
 */
@Schema(description = "Request body for token refresh")
public record RefreshTokenRequest(

        @NotBlank
        @Schema(description = "Opaque refresh token", example = "dGhpcyBpcyBh...")
        String refreshToken) {
}
