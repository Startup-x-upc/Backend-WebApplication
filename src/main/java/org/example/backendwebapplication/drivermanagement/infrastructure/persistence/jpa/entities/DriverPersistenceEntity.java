package org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

/**
 * JPA Persistence entity for Driver aggregate.
 */
@Getter
@Entity
@Table(name = "drivers")
public class DriverPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "driver_id", nullable = false, unique = true, length = 36)
    private String driverId;

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "access_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DriverAccessStatus accessStatus;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Column(name = "rating_average", nullable = false)
    private double ratingAverage;

    @Column(name = "rating_count", nullable = false)
    private int ratingCount;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "soat_number", nullable = false)
    private String soatNumber;

    @Column(name = "is_busy", nullable = false)
    private boolean isBusy;

    @Column(name = "active_ride_id", length = 36)
    private String activeRideId;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "restriction_reason")
    private String restrictionReason;

    public DriverPersistenceEntity() {}

    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setAccessStatus(DriverAccessStatus accessStatus) { this.accessStatus = accessStatus; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
    public void setRatingAverage(double ratingAverage) { this.ratingAverage = ratingAverage; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setSoatNumber(String soatNumber) { this.soatNumber = soatNumber; }
    public void setBusy(boolean busy) { this.isBusy = busy; }
    public void setActiveRideId(String activeRideId) { this.activeRideId = activeRideId; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public void setRestrictionReason(String restrictionReason) { this.restrictionReason = restrictionReason; }
}
