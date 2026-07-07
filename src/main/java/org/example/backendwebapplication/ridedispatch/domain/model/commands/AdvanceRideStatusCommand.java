package org.example.backendwebapplication.ridedispatch.domain.model.commands;

import java.util.UUID;

public record AdvanceRideStatusCommand(
        UUID rideId,
        UUID driverId,
        String status
) {}
