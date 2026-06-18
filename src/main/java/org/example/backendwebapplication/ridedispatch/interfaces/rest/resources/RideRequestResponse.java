package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import java.util.UUID;

public record RideRequestResponse(
        UUID id,
        UUID passengerId,
        String passengerName,
        String passengerPhotoUrl,
        String origin,
        String destination,
        double distanceKm,
        double estimatedFare,
        String status,
        boolean isExpired,
        String createdAt
) {}
