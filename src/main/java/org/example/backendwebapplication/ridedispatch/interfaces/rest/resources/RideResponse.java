package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import java.util.UUID;

public record RideResponse(
        UUID id,
        UUID requestId,
        UUID passengerId,
        UUID driverId,
        String driverName,
        String passengerName,
        String origin,
        String destination,
        double estimatedFare,
        String status,
        String createdAt,
        String completedAt
) {}
