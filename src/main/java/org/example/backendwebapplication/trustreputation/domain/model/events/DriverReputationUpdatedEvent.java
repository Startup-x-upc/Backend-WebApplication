package org.example.backendwebapplication.trustreputation.domain.model.events;

import java.util.UUID;

public record DriverReputationUpdatedEvent(
        UUID driverId,
        double averageScore,
        long totalRatings
) {}
