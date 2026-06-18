package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

@Getter
@Entity
@Table(name = "driver_availability")
public class DriverAvailabilityPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "driver_availability_id", nullable = false, unique = true, length = 36)
    private String driverAvailabilityId;

    @Column(name = "driver_id", nullable = false, unique = true, length = 36)
    private String driverId;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @Column(name = "is_busy", nullable = false)
    private boolean isBusy;

    @Column(name = "active_ride_id", length = 36)
    private String activeRideId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public DriverAvailabilityPersistenceEntity() {}

    public void setDriverAvailabilityId(String driverAvailabilityId) { this.driverAvailabilityId = driverAvailabilityId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setBusy(boolean busy) { isBusy = busy; }
    public void setActiveRideId(String activeRideId) { this.activeRideId = activeRideId; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
