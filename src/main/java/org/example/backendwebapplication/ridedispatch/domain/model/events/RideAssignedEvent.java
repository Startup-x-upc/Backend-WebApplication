package org.example.backendwebapplication.ridedispatch.domain.model.events;

import java.util.UUID;

/**
 * Event published when a driver is assigned to a ride.
 */
public record RideAssignedEvent(
        UUID rideId,
        UUID driverId
) {}
