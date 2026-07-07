package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RidePersistenceEntity;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class RidePersistenceAssembler {

    public static RidePersistenceEntity toEntity(Ride domain) {
        if (domain == null) return null;
        RidePersistenceEntity entity = new RidePersistenceEntity();
        entity.setRideId(domain.getId().toString());
        entity.setRideRequestId(domain.getRequestId().toString());
        entity.setPassengerId(domain.getPassengerId().toString());
        entity.setDriverId(domain.getDriverId().toString());
        entity.setOrigin(domain.getOrigin());
        entity.setDestination(domain.getDestination());
        entity.setEstimatedFare(domain.getEstimatedFare());
        entity.setStatus(domain.getStatus());
        entity.setCompletedAt(domain.getCompletedAt() != null ? Date.from(domain.getCompletedAt()) : null);
        return entity;
    }

    public static Ride toDomain(RidePersistenceEntity entity) {
        if (entity == null) return null;
        return new Ride(
                UUID.fromString(entity.getRideId()),
                UUID.fromString(entity.getRideRequestId()),
                UUID.fromString(entity.getPassengerId()),
                UUID.fromString(entity.getDriverId()),
                entity.getOrigin(),
                entity.getDestination(),
                entity.getEstimatedFare(),
                entity.getStatus(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : Instant.now(),
                entity.getCompletedAt() != null ? entity.getCompletedAt().toInstant() : null
        );
    }
}
