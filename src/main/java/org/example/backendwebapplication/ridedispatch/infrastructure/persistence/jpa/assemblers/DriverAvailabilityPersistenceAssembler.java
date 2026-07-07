package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.DriverAvailabilityPersistenceEntity;

import java.util.UUID;

public class DriverAvailabilityPersistenceAssembler {

    public static DriverAvailabilityPersistenceEntity toEntity(DriverAvailability domain) {
        if (domain == null) return null;
        DriverAvailabilityPersistenceEntity entity = new DriverAvailabilityPersistenceEntity();
        entity.setDriverAvailabilityId(domain.getId().toString());
        entity.setDriverId(domain.getDriverId().toString());
        entity.setAvailable(domain.isAvailable());
        entity.setBusy(domain.isBusy());
        entity.setActiveRideId(domain.getActiveRideId() != null ? domain.getActiveRideId().toString() : null);
        entity.setLatitude(domain.getLatitude());
        entity.setLongitude(domain.getLongitude());
        return entity;
    }

    public static DriverAvailability toDomain(DriverAvailabilityPersistenceEntity entity) {
        if (entity == null) return null;
        return new DriverAvailability(
                UUID.fromString(entity.getDriverAvailabilityId()),
                UUID.fromString(entity.getDriverId()),
                entity.isAvailable(),
                entity.isBusy(),
                entity.getActiveRideId() != null ? UUID.fromString(entity.getActiveRideId()) : null,
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
