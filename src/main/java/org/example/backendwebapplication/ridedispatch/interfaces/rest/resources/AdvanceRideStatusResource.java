package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record AdvanceRideStatusResource(
        @NotBlank String status
) {}
