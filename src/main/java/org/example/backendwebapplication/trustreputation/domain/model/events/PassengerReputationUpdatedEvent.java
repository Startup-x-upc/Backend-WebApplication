package org.example.backendwebapplication.trustreputation.domain.model.events;

import java.util.UUID;

public record PassengerReputationUpdatedEvent(
        UUID passengerId,
        double averageScore,
        long totalRatings
) {}
