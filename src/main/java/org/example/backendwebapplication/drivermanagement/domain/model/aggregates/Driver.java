package org.example.backendwebapplication.drivermanagement.domain.model.aggregates;

import org.example.backendwebapplication.drivermanagement.domain.model.events.DriverAvailabilityChangedEvent;
import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.UUID;

/**
 * Driver aggregate root.
 * <p>Represents a driver registered on the platform, carrying status, reputation,
 * availability, and verification document details.</p>
 */
public class Driver extends AbstractDomainAggregateRoot<Driver> {

    private UUID driverId;
    private UUID userId;
    private String fullName;
    private String vehicleType;
    private DriverAccessStatus accessStatus;
    private boolean isAvailable;
    private double ratingAverage;
    private int ratingCount;
    private String photoUrl;
    private String licenseNumber;
    private String soatNumber;
    
    // Dynamic status fields
    private boolean isBusy;
    private UUID activeRideId;
    private String restrictionReason;
    
    private Instant createdAt;

    /** JPA-friendly constructor. */
    public Driver() {
    }

    /**
     * Creates a new Driver.
     */
    public Driver(UUID userId, String fullName, String vehicleType, String licenseNumber, String soatNumber) {
        this.driverId = UUID.randomUUID();
        this.userId = userId;
        this.fullName = fullName;
        this.vehicleType = vehicleType;
        this.accessStatus = DriverAccessStatus.ACTIVE;
        this.isAvailable = false;
        this.ratingAverage = 0.0;
        this.ratingCount = 0;
        this.photoUrl = "";
        this.licenseNumber = licenseNumber;
        this.soatNumber = soatNumber;
        this.isBusy = false;
        this.activeRideId = null;
        this.restrictionReason = "";
        this.createdAt = Instant.now();
    }

    /**
     * Reconstitution constructor (used by persistence assembler).
     */
    public Driver(UUID driverId, UUID userId, String fullName, String vehicleType,
                  DriverAccessStatus accessStatus, boolean isAvailable, double ratingAverage,
                  int ratingCount, String photoUrl, String licenseNumber, String soatNumber,
                  boolean isBusy, UUID activeRideId, String restrictionReason,
                  Instant createdAt) {
        this.driverId = driverId;
        this.userId = userId;
        this.fullName = fullName;
        this.vehicleType = vehicleType;
        this.accessStatus = accessStatus;
        this.isAvailable = isAvailable;
        this.ratingAverage = ratingAverage;
        this.ratingCount = ratingCount;
        this.photoUrl = (photoUrl != null) ? photoUrl : "";
        this.licenseNumber = licenseNumber;
        this.soatNumber = soatNumber;
        this.isBusy = isBusy;
        this.activeRideId = activeRideId;
        this.restrictionReason = (restrictionReason != null) ? restrictionReason : "";
        this.createdAt = createdAt;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public UUID getDriverId() { return driverId; }
    public UUID getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getVehicleType() { return vehicleType; }
    public DriverAccessStatus getAccessStatus() { return accessStatus; }
    public boolean isAvailable() { return isAvailable; }
    public double getRatingAverage() { return ratingAverage; }
    public int getRatingCount() { return ratingCount; }
    public String getPhotoUrl() { return photoUrl; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getSoatNumber() { return soatNumber; }
    public boolean isBusy() { return isBusy; }
    public UUID getActiveRideId() { return activeRideId; }
    public String getRestrictionReason() { return restrictionReason; }
    public Instant getCreatedAt() { return createdAt; }

    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public void setUserId(UUID userId) { this.userId = userId; }
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
    public void setActiveRideId(UUID activeRideId) { this.activeRideId = activeRideId; }
    public void setRestrictionReason(String restrictionReason) { this.restrictionReason = restrictionReason; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    // ── Domain Behaviour ─────────────────────────────────────────────────

    /**
     * Toggles availability of the driver.
     */
    public void toggleAvailability() {
        if (this.accessStatus == DriverAccessStatus.RESTRICTED) {
            throw new IllegalStateException("Driver is restricted and cannot change availability status");
        }
        this.isAvailable = !this.isAvailable;
        registerDomainEvent(new DriverAvailabilityChangedEvent(this.driverId, this.isAvailable));
    }

    /**
     * Force sets availability to false (e.g. on wallet empty event).
     */
    public void forceDeactivateAvailability() {
        if (this.isAvailable) {
            this.isAvailable = false;
            registerDomainEvent(new DriverAvailabilityChangedEvent(this.driverId, this.isAvailable));
        }
    }

    /**
     * Restricts the driver.
     */
    public void restrict(String reason) {
        this.accessStatus = DriverAccessStatus.RESTRICTED;
        this.restrictionReason = reason;
        this.isAvailable = false;
        registerDomainEvent(new DriverAvailabilityChangedEvent(this.driverId, false));
    }

    /**
     * Unrestricts the driver.
     */
    public void unrestrict() {
        this.accessStatus = DriverAccessStatus.ACTIVE;
        this.restrictionReason = "";
    }

    /**
     * Syncs display details from Profile.
     */
    public void updateProfile(String fullName, String photoUrl) {
        this.fullName = fullName;
        if (photoUrl != null) {
            this.photoUrl = photoUrl;
        }
    }
}
