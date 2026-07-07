package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.util.UUID;

public record RideRequestExpiredEvent(
        UUID requestId,
        UUID passengerId
) {}
