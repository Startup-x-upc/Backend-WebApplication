package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event published when a ride is successfully completed.
 */
public record RideCompletedEvent(
        UUID rideId,
        UUID driverId,
        BigDecimal estimatedFare
) {}
