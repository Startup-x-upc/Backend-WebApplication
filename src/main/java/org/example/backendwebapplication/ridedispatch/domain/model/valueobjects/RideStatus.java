package org.example.backendwebapplication.ridedispatch.domain.model.valueobjects;

/**
 * Represents the lifecycle status of a ride request and ride.
 */
public enum RideStatus {
    PENDING,
    OPEN,
    CONFIRMED,
    ACCEPTED,
    DRIVER_ON_THE_WAY,
    DRIVER_ARRIVED,
    STARTED,
    COMPLETED,
    CANCELLED
}
