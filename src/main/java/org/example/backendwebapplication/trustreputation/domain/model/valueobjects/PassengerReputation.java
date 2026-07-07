package org.example.backendwebapplication.trustreputation.domain.model.valueobjects;

import java.util.UUID;

public record PassengerReputation(
        UUID passengerId,
        double averageScore,
        long totalRatings
) {}
