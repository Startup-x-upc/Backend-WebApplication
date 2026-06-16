package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST resource for profile update requests.
 *
 * @param fullName new display name
 * @param photoUrl new photo URL (optional)
 */
@Schema(description = "Request body for profile update")
public record UpdateProfileResource(

        @NotBlank
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        @Schema(description = "New display name", example = "María Quispe Actualizado")
        String fullName,

        @Schema(description = "New profile photo URL", example = "https://i.pravatar.cc/150?img=47")
        String photoUrl) {
}
