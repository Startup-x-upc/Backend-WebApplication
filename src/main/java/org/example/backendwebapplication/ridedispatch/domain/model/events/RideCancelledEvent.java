package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.util.UUID;

/**
 * Event published when a ride is canceled before starting.
 */
public record RideCancelledEvent(
        UUID rideId,
        UUID driverId
) {}
