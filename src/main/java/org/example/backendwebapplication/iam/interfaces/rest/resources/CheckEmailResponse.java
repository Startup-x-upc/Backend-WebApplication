package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST resource for the email existence check.
 *
 * @param exists whether the email is already registered
 */
@Schema(description = "Email existence check response")
public record CheckEmailResponse(

        @Schema(description = "Whether the email is already registered", example = "true")
        boolean exists) {
}
