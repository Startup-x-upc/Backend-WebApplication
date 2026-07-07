package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.util.UUID;

public record DriverAppliedEvent(
        UUID requestId,
        UUID driverId
) {}
