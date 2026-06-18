package org.example.backendwebapplication.ridedispatch.domain.model.commands;

import java.util.UUID;

public record CreateRideRequestCommand(
        UUID passengerId,
        String origin,
        String destination,
        double distanceKm,
        double estimatedFare
) {}
