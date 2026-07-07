package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import java.util.UUID;

public record RideCandidateResponse(
        UUID id,
        UUID requestId,
        UUID driverId,
        String driverName,
        String vehicleType,
        double ratingAverage,
        String photoUrl,
        String status,
        String appliedAt
) {}
