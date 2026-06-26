package org.example.backendwebapplication.trustreputation.interfaces.rest.resources;

import java.util.UUID;

public record PassengerReputationResponse(
        UUID passengerId,
        double averageScore,
        long totalRatings
) {}
