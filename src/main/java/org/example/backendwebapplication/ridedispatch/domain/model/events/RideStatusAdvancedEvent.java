package org.example.backendwebapplication.ridedispatch.domain.model.events;

import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import java.util.UUID;

public record RideStatusAdvancedEvent(
        UUID rideId,
        UUID driverId,
        UUID passengerId,
        RideStatus status
) {}
