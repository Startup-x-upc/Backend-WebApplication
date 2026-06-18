package org.example.backendwebapplication.drivermanagement.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record DriverResponse(
        UUID id,
        UUID userId,
        String fullName,
        String vehicleType,
        String accessStatus,
        boolean isAvailable,
        double ratingAverage,
        int ratingCount,
        String photoUrl,
        String licenseNumber,
        String soatNumber,
        boolean isBusy,
        UUID activeRideId,
        String currentLocation,
        String restrictionReason,
        Instant createdAt
) {}
