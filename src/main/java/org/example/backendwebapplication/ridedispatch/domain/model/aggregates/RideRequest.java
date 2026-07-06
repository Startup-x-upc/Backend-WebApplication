package org.example.backendwebapplication.ridedispatch.domain.model.aggregates;

import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.domain.model.events.DriverAppliedEvent;
import org.example.backendwebapplication.ridedispatch.domain.model.events.RideRequestCreatedEvent;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RideRequest aggregate root.
 * Represents a passenger's open request for a ride.
 */
public class RideRequest extends AbstractDomainAggregateRoot<RideRequest> {

    private UUID id;
    private UUID passengerId;
    private UUID selectedDriverId;
    private String origin;
    private String destination;
    private double distanceKm;
    private double estimatedFare;
    private RideStatus status;
    private boolean isExpired;
    private Instant createdAt;
    private List<RideCandidate> candidates = new ArrayList<>();

    public RideRequest() {}

    public RideRequest(UUID passengerId, String origin, String destination, double distanceKm, double estimatedFare) {
        this.id = UUID.randomUUID();
        this.passengerId = passengerId;
        this.selectedDriverId = null;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedFare = estimatedFare;
        this.status = RideStatus.OPEN;
        this.isExpired = false;
        this.createdAt = Instant.now();
        registerDomainEvent(new RideRequestCreatedEvent(this.id, this.passengerId, this.origin, this.destination, this.distanceKm, this.estimatedFare));
    }

    public RideRequest(UUID id, UUID passengerId, UUID selectedDriverId, String origin, String destination,
                       double distanceKm, double estimatedFare, RideStatus status, boolean isExpired,
                       Instant createdAt, List<RideCandidate> candidates) {
        this.id = id;
        this.passengerId = passengerId;
        this.selectedDriverId = selectedDriverId;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedFare = estimatedFare;
        this.status = status;
        this.isExpired = isExpired;
        this.createdAt = createdAt;
        this.candidates = candidates != null ? new ArrayList<>(candidates) : new ArrayList<>();
    }

    // ── Business Domain Logic ─────────────────────────────────────────────────

    /**
     * Adds a driver application to this request.
     */
    public RideCandidate applyCandidate(UUID driverId, String driverName, String vehicleType,
                                        double ratingAverage, String photoUrl) {
        if (this.isExpired) {
            throw new IllegalStateException("REQUEST_EXPIRED: La solicitud ya expiró");
        }
        if (this.status != RideStatus.OPEN) {
            throw new IllegalStateException("REQUEST_NOT_OPEN: La solicitud ya no está abierta");
        }
        boolean alreadyApplied = this.candidates.stream()
                .anyMatch(c -> c.getDriverId().equals(driverId));
        if (alreadyApplied) {
            throw new IllegalStateException("ALREADY_APPLIED: Ya aplicaste a este request");
        }

        RideCandidate candidate = new RideCandidate(this.id, driverId, driverName, vehicleType, ratingAverage, photoUrl);
        this.candidates.add(candidate);
        registerDomainEvent(new DriverAppliedEvent(this.id, driverId));
        return candidate;
    }

    /**
     * Selects a candidate to assign to the ride, updating other candidates as rejected.
     */
    public RideCandidate selectCandidate(UUID candidateId) {
        if (this.status != RideStatus.OPEN) {
            throw new IllegalStateException("REQUEST_NOT_OPEN: El request ya fue confirmado o expiró");
        }
        if (this.isExpired) {
            throw new IllegalStateException("REQUEST_EXPIRED: El request expiró");
        }

        RideCandidate selected = this.candidates.stream()
                .filter(c -> c.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CANDIDATE_NOT_FOUND: Candidato no encontrado"));

        selected.accept();
        this.selectedDriverId = selected.getDriverId();
        this.status = RideStatus.CONFIRMED;

        // Reject other candidates
        this.candidates.stream()
                .filter(c -> !c.getId().equals(candidateId))
                .forEach(RideCandidate::reject);

        return selected;
    }

    /**
     * Flags the request as expired if it is still open.
     */
    public void expire() {
        if (this.status == RideStatus.OPEN) {
            this.isExpired = true;
        }
    }

    /**
     * Cancels the request if it is still open.
     */
    public void cancel() {
        if (this.status != RideStatus.OPEN) {
            throw new IllegalStateException("REQUEST_NOT_OPEN: La solicitud ya no está abierta");
        }
        this.status = RideStatus.CANCELLED;
    }

    /**
     * Withdraws driver candidacy from this request.
     */
    public void withdrawCandidate(UUID driverId) {
        if (this.status != RideStatus.OPEN) {
            throw new IllegalStateException("REQUEST_NOT_OPEN: La solicitud ya no está abierta");
        }
        if (this.isExpired) {
            throw new IllegalStateException("REQUEST_EXPIRED: La solicitud ya expiró");
        }
        boolean removed = this.candidates.removeIf(c -> c.getDriverId().equals(driverId));
        if (!removed) {
            throw new IllegalArgumentException("CANDIDATE_NOT_FOUND: No eres candidato de esta solicitud");
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public UUID getPassengerId() { return passengerId; }
    public UUID getSelectedDriverId() { return selectedDriverId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getDistanceKm() { return distanceKm; }
    public double getEstimatedFare() { return estimatedFare; }
    public RideStatus getStatus() { return status; }
    public boolean isExpired() { return isExpired; }
    public Instant getCreatedAt() { return createdAt; }
    public List<RideCandidate> getCandidates() { return candidates; }

    public void setId(UUID id) { this.id = id; }
    public void setPassengerId(UUID passengerId) { this.passengerId = passengerId; }
    public void setSelectedDriverId(UUID selectedDriverId) { this.selectedDriverId = selectedDriverId; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public void setEstimatedFare(double estimatedFare) { this.estimatedFare = estimatedFare; }
    public void setStatus(RideStatus status) { this.status = status; }
    public void setExpired(boolean expired) { isExpired = expired; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setCandidates(List<RideCandidate> candidates) { this.candidates = candidates; }
}
