package org.example.backendwebapplication.drivermanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record RestrictDriverResource(
        @NotBlank(message = "Restriction reason is required")
        String reason
) {}
