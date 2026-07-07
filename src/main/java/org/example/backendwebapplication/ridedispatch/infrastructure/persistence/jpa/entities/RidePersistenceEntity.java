package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

import java.util.Date;

@Getter
@Entity
@Table(name = "rides")
public class RidePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "ride_id", nullable = false, unique = true, length = 36)
    private String rideId;

    @Column(name = "ride_request_id", nullable = false, length = 36)
    private String rideRequestId;

    @Column(name = "passenger_id", nullable = false, length = 36)
    private String passengerId;

    @Column(name = "driver_id", nullable = false, length = 36)
    private String driverId;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "estimated_fare", nullable = false)
    private double estimatedFare;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    public RidePersistenceEntity() {}

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setRideRequestId(String rideRequestId) { this.rideRequestId = rideRequestId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setEstimatedFare(double estimatedFare) { this.estimatedFare = estimatedFare; }
    public void setStatus(RideStatus status) { this.status = status; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
}
