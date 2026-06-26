package org.example.backendwebapplication.trustreputation.interfaces.rest.resources;

import java.util.UUID;

public record TripRatingResponse(
        UUID id,
        UUID tripId,
        UUID driverId,
        UUID passengerId,
        String driverRatingStatus,
        String passengerRatingStatus,
        Integer driverScore,
        Integer passengerScore,
        String passengerComment,
        String rateableUntil,
        String createdAt
) {}
