package org.example.backendwebapplication.ridedispatch.domain.model.commands;

import java.util.UUID;

public record CancelRideCommand(
        UUID rideId,
        UUID requesterId
) {}
