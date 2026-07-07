package org.example.backendwebapplication.ridedispatch.domain.model.commands;

import java.util.UUID;

public record CancelRideRequestCommand(
        UUID requestId,
        UUID passengerId
) {}
