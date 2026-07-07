package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRideRequestResource(
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull @Min(0) Double distanceKm,
        @NotNull @Min(0) Double estimatedFare
) {}
