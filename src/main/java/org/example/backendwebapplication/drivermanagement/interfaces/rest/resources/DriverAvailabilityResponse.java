package org.example.backendwebapplication.drivermanagement.interfaces.rest.resources;

import java.util.UUID;

public record DriverAvailabilityResponse(
        UUID id,
        UUID userId,
        boolean isAvailable,
        boolean isBusy,
        UUID activeRideId
) {}
