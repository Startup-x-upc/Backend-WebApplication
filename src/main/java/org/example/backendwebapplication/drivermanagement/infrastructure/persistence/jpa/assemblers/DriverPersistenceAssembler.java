package org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * Assembler class for converting between domain and persistence layers of Driver context.
 */
public class DriverPersistenceAssembler {

    public static DriverPersistenceEntity toEntity(Driver domain) {
        DriverPersistenceEntity entity = new DriverPersistenceEntity();
        entity.setDriverId(domain.getDriverId().toString());
        entity.setUserId(domain.getUserId().toString());
        entity.setFullName(domain.getFullName());
        entity.setVehicleType(domain.getVehicleType());
        entity.setAccessStatus(domain.getAccessStatus());
        entity.setAvailable(domain.isAvailable());
        entity.setRatingAverage(domain.getRatingAverage());
        entity.setRatingCount(domain.getRatingCount());
        entity.setPhotoUrl(domain.getPhotoUrl());
        entity.setLicenseNumber(domain.getLicenseNumber());
        entity.setSoatNumber(domain.getSoatNumber());
        entity.setBusy(domain.isBusy());
        entity.setActiveRideId(domain.getActiveRideId() != null ? domain.getActiveRideId().toString() : null);
        entity.setRestrictionReason(domain.getRestrictionReason());
        return entity;
    }

    public static Driver toDomain(DriverPersistenceEntity entity) {
        return new Driver(
                UUID.fromString(entity.getDriverId()),
                UUID.fromString(entity.getUserId()),
                entity.getFullName(),
                entity.getVehicleType(),
                entity.getAccessStatus(),
                entity.isAvailable(),
                entity.getRatingAverage(),
                entity.getRatingCount(),
                entity.getPhotoUrl(),
                entity.getLicenseNumber(),
                entity.getSoatNumber(),
                entity.isBusy(),
                entity.getActiveRideId() != null ? UUID.fromString(entity.getActiveRideId()) : null,
                entity.getRestrictionReason(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : Instant.now()
        );
    }
}
