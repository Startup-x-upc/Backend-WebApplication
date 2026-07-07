package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "ride_requests")
public class RideRequestPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "passenger_id", nullable = false, length = 36)
    private String passengerId;

    @Column(name = "ride_request_id", nullable = false, unique = true, length = 36)
    private String rideRequestId;

    @Column(name = "selected_driver_id", length = 36)
    private String selectedDriverId;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "distance_km", nullable = false)
    private double distanceKm;

    @Column(name = "estimated_fare", nullable = false)
    private double estimatedFare;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(name = "is_expired", nullable = false)
    private boolean isExpired;

    @OneToMany(mappedBy = "rideRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RideCandidatePersistenceEntity> candidates = new ArrayList<>();

    public RideRequestPersistenceEntity() {}

    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setRideRequestId(String rideRequestId) { this.rideRequestId = rideRequestId; }
    public void setSelectedDriverId(String selectedDriverId) { this.selectedDriverId = selectedDriverId; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public void setEstimatedFare(double estimatedFare) { this.estimatedFare = estimatedFare; }
    public void setStatus(RideStatus status) { this.status = status; }
    public void setExpired(boolean expired) { isExpired = expired; }
    public void setCandidates(List<RideCandidatePersistenceEntity> candidates) {
        this.candidates = candidates;
        if (candidates != null) {
            candidates.forEach(c -> c.setRideRequest(this));
        }
    }
}
