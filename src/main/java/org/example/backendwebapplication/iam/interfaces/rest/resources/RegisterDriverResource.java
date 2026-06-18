package org.example.backendwebapplication.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST resource for driver registration requests.
 *
 * @param email         valid email address
 * @param password      minimum 6 characters
 * @param fullName      display name (2–100 characters)
 * @param vehicleType   type of vehicle
 * @param licenseNumber driver's license number
 * @param soatNumber    SOAT insurance number
 */
@Schema(description = "Request body for driver registration")
public record RegisterDriverResource(

        @Email
        @NotBlank
        @Schema(description = "User's email address", example = "conductor@correo.com")
        String email,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "User's password (min 6 characters)", example = "pass123")
        String password,

        @NotBlank
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        @Schema(description = "Driver's full display name", example = "Carlos Mendoza")
        String fullName,

        @NotBlank
        @Size(min = 2)
        @Schema(description = "Vehicle type", example = "Mototaxi")
        String vehicleType,

        @NotBlank
        @Schema(description = "Driver's license number", example = "Q12345678")
        String licenseNumber,

        @NotBlank
        @Schema(description = "SOAT insurance number", example = "S987654321")
        String soatNumber) {
}
