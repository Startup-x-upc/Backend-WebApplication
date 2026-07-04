package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.util.UUID;

public record RideRequestCreatedEvent(
        UUID requestId,
        UUID passengerId,
        String origin,
        String destination,
        double distanceKm,
        double estimatedFare
) {}
