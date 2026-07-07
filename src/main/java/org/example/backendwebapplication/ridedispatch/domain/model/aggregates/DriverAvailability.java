package org.example.backendwebapplication.ridedispatch.domain.model.aggregates;

import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.util.UUID;

/**
 * DriverAvailability aggregate root.
 * Tracks driver real-time availability, busy status, and active ride assignments in the Ride Dispatch context.
 */
public class DriverAvailability extends AbstractDomainAggregateRoot<DriverAvailability> {

    private UUID id;
    private UUID driverId;
    private boolean isAvailable;
    private boolean isBusy;
    private UUID activeRideId;
    private Double latitude;
    private Double longitude;

    public DriverAvailability() {}

    public DriverAvailability(UUID driverId) {
        this.id = UUID.randomUUID();
        this.driverId = driverId;
        this.isAvailable = false;
        this.isBusy = false;
        this.activeRideId = null;
        this.latitude = null;
        this.longitude = null;
    }

    public DriverAvailability(UUID id, UUID driverId, boolean isAvailable, boolean isBusy,
                              UUID activeRideId, Double latitude, Double longitude) {
        this.id = id;
        this.driverId = driverId;
        this.isAvailable = isAvailable;
        this.isBusy = isBusy;
        this.activeRideId = activeRideId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ── Business Domain Logic ─────────────────────────────────────────────────

    /**
     * Synchronizes availability from the Driver Management context.
     */
    public void syncAvailability(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    /**
     * Assigns an active ride to the driver, marking them as busy.
     */
    public void assignRide(UUID rideId) {
        this.isBusy = true;
        this.activeRideId = rideId;
    }

    /**
     * Clears the active ride assignment and frees the driver's busy state.
     */
    public void clearRide() {
        this.isBusy = false;
        this.activeRideId = null;
    }

    /**
     * Updates the driver's location.
     */
    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public UUID getDriverId() { return driverId; }
    public boolean isAvailable() { return isAvailable; }
    public boolean isBusy() { return isBusy; }
    public UUID getActiveRideId() { return activeRideId; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setId(UUID id) { this.id = id; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setBusy(boolean busy) { isBusy = busy; }
    public void setActiveRideId(UUID activeRideId) { this.activeRideId = activeRideId; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
