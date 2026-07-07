package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.util.UUID;

public record DriverWithdrewEvent(
        UUID requestId,
        UUID driverId
) {}
