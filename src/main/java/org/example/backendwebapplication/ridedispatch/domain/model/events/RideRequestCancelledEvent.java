package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.util.UUID;

public record RideRequestCancelledEvent(
        UUID requestId,
        UUID passengerId
) {}
