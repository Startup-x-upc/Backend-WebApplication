package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.CandidateStatus;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

@Getter
@Entity
@Table(name = "ride_candidates")
public class RideCandidatePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "candidate_id", nullable = false, unique = true, length = 36)
    private String candidateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_request_id", nullable = false)
    private RideRequestPersistenceEntity rideRequest;

    @Column(name = "driver_id", nullable = false, length = 36)
    private String driverId;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "rating_average", nullable = false)
    private double ratingAverage;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CandidateStatus status;

    public RideCandidatePersistenceEntity() {}

    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    public void setRideRequest(RideRequestPersistenceEntity rideRequest) { this.rideRequest = rideRequest; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setRatingAverage(double ratingAverage) { this.ratingAverage = ratingAverage; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setStatus(CandidateStatus status) { this.status = status; }
}
