package org.example.backendwebapplication.trustreputation.domain.model.valueobjects;

import java.util.UUID;

public record DriverReputation(
        UUID driverId,
        double averageScore,
        long totalRatings
) {}
