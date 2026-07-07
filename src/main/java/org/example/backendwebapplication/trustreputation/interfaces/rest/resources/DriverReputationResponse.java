package org.example.backendwebapplication.trustreputation.interfaces.rest.resources;

import java.util.UUID;

public record DriverReputationResponse(
        UUID driverId,
        double averageScore,
        long totalRatings
) {}
