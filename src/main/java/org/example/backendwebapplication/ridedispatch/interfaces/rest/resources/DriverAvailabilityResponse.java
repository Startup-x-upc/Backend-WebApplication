package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import java.util.UUID;

public record DriverAvailabilityResponse(
        UUID id,
        UUID driverId,
        boolean isAvailable,
        boolean isBusy,
        UUID activeRideId,
        Double latitude,
        Double longitude
) {}
