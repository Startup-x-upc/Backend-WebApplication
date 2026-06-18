package org.example.backendwebapplication.ridedispatch.domain.model.entities;

import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.CandidateStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a driver applying to pick up a passenger's ride request.
 */
public class RideCandidate {

    private UUID id;
    private UUID requestId;
    private UUID driverId;
    private String driverName;
    private String vehicleType;
    private double ratingAverage;
    private String photoUrl;
    private CandidateStatus status;
    private Instant appliedAt;

    public RideCandidate() {}

    public RideCandidate(UUID requestId, UUID driverId, String driverName, String vehicleType,
                         double ratingAverage, String photoUrl) {
        this.id = UUID.randomUUID();
        this.requestId = requestId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.vehicleType = vehicleType;
        this.ratingAverage = ratingAverage;
        this.photoUrl = photoUrl != null ? photoUrl : "";
        this.status = CandidateStatus.PROPOSED;
        this.appliedAt = Instant.now();
    }

    public RideCandidate(UUID id, UUID requestId, UUID driverId, String driverName, String vehicleType,
                         double ratingAverage, String photoUrl, CandidateStatus status, Instant appliedAt) {
        this.id = id;
        this.requestId = requestId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.vehicleType = vehicleType;
        this.ratingAverage = ratingAverage;
        this.photoUrl = photoUrl != null ? photoUrl : "";
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public void accept() {
        this.status = CandidateStatus.ACCEPTED;
    }

    public void reject() {
        this.status = CandidateStatus.REJECTED;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public UUID getRequestId() { return requestId; }
    public UUID getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }
    public String getVehicleType() { return vehicleType; }
    public double getRatingAverage() { return ratingAverage; }
    public String getPhotoUrl() { return photoUrl; }
    public CandidateStatus getStatus() { return status; }
    public Instant getAppliedAt() { return appliedAt; }

    public void setId(UUID id) { this.id = id; }
    public void setRequestId(UUID requestId) { this.requestId = requestId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setRatingAverage(double ratingAverage) { this.ratingAverage = ratingAverage; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setStatus(CandidateStatus status) { this.status = status; }
    public void setAppliedAt(Instant appliedAt) { this.appliedAt = appliedAt; }
}
