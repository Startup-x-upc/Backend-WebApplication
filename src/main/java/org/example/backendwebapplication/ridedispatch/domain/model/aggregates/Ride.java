package org.example.backendwebapplication.ridedispatch.domain.model.aggregates;

import org.example.backendwebapplication.ridedispatch.domain.model.events.RideAssignedEvent;
import org.example.backendwebapplication.ridedispatch.domain.model.events.RideCancelledEvent;
import org.example.backendwebapplication.ridedispatch.domain.model.events.RideCompletedEvent;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Ride aggregate root.
 * Manages active rides, their progression, and cancellation.
 */
public class Ride extends AbstractDomainAggregateRoot<Ride> {

    private UUID id;
    private UUID requestId;
    private UUID passengerId;
    private UUID driverId;
    private String origin;
    private String destination;
    private double estimatedFare;
    private RideStatus status;
    private Instant createdAt;
    private Instant completedAt;

    public Ride() {}

    public Ride(UUID requestId, UUID passengerId, UUID driverId, String origin, String destination, double estimatedFare) {
        this.id = UUID.randomUUID();
        this.requestId = requestId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.origin = origin;
        this.destination = destination;
        this.estimatedFare = estimatedFare;
        this.status = RideStatus.ACCEPTED;
        this.createdAt = Instant.now();
        this.completedAt = null;
        registerDomainEvent(new RideAssignedEvent(this.id, this.driverId));
    }

    public Ride(UUID id, UUID requestId, UUID passengerId, UUID driverId, String origin, String destination,
                double estimatedFare, RideStatus status, Instant createdAt, Instant completedAt) {
        this.id = id;
        this.requestId = requestId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.origin = origin;
        this.destination = destination;
        this.estimatedFare = estimatedFare;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    // ── Business Domain Logic ─────────────────────────────────────────────────

    /**
     * Advances the status of the ride sequentially.
     */
    public void advanceStatus(RideStatus targetStatus) {
        if (this.status == RideStatus.COMPLETED || this.status == RideStatus.CANCELLED) {
            throw new IllegalStateException("INVALID_TRANSITION: No se puede avanzar si el ride está CANCELLED o COMPLETED");
        }

        boolean isValid = false;
        if (this.status == RideStatus.ACCEPTED && targetStatus == RideStatus.DRIVER_ON_THE_WAY) {
            isValid = true;
        } else if (this.status == RideStatus.DRIVER_ON_THE_WAY && targetStatus == RideStatus.DRIVER_ARRIVED) {
            isValid = true;
        } else if (this.status == RideStatus.DRIVER_ARRIVED && targetStatus == RideStatus.STARTED) {
            isValid = true;
        } else if (this.status == RideStatus.STARTED && targetStatus == RideStatus.COMPLETED) {
            isValid = true;
            this.completedAt = Instant.now();
            registerDomainEvent(new RideCompletedEvent(this.id, this.driverId, BigDecimal.valueOf(this.estimatedFare)));
        }

        if (!isValid) {
            throw new IllegalStateException("INVALID_TRANSITION: Transición de estado no permitida");
        }

        this.status = targetStatus;
    }

    /**
     * Cancels the ride if it hasn't started yet.
     */
    public void cancel() {
        if (this.status == RideStatus.STARTED || this.status == RideStatus.COMPLETED || this.status == RideStatus.CANCELLED) {
            throw new IllegalStateException("CANNOT_CANCEL: El ride ya empezó o ya fue cancelado");
        }
        this.status = RideStatus.CANCELLED;
        registerDomainEvent(new RideCancelledEvent(this.id, this.driverId));
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public UUID getRequestId() { return requestId; }
    public UUID getPassengerId() { return passengerId; }
    public UUID getDriverId() { return driverId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getEstimatedFare() { return estimatedFare; }
    public RideStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getCompletedAt() { return completedAt; }

    public void setId(UUID id) { this.id = id; }
    public void setRequestId(UUID requestId) { this.requestId = requestId; }
    public void setPassengerId(UUID passengerId) { this.passengerId = passengerId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setEstimatedFare(double estimatedFare) { this.estimatedFare = estimatedFare; }
    public void setStatus(RideStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
