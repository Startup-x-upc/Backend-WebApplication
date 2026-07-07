package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RideCandidatePersistenceEntity;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RideRequestPersistenceEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RideRequestPersistenceAssembler {

    public static RideRequestPersistenceEntity toEntity(RideRequest domain) {
        if (domain == null) return null;
        RideRequestPersistenceEntity entity = new RideRequestPersistenceEntity();
        entity.setPassengerId(domain.getPassengerId().toString());
        entity.setRideRequestId(domain.getId().toString());
        entity.setSelectedDriverId(domain.getSelectedDriverId() != null ? domain.getSelectedDriverId().toString() : null);
        entity.setOrigin(domain.getOrigin());
        entity.setDestination(domain.getDestination());
        entity.setDistanceKm(domain.getDistanceKm());
        entity.setEstimatedFare(domain.getEstimatedFare());
        entity.setStatus(domain.getStatus());
        entity.setExpired(domain.isExpired());

        if (domain.getCandidates() != null) {
            List<RideCandidatePersistenceEntity> candidates = domain.getCandidates().stream()
                    .map(c -> {
                        RideCandidatePersistenceEntity cEntity = new RideCandidatePersistenceEntity();
                        cEntity.setCandidateId(c.getId().toString());
                        cEntity.setDriverId(c.getDriverId().toString());
                        cEntity.setDriverName(c.getDriverName());
                        cEntity.setVehicleType(c.getVehicleType());
                        cEntity.setRatingAverage(c.getRatingAverage());
                        cEntity.setPhotoUrl(c.getPhotoUrl());
                        cEntity.setStatus(c.getStatus());
                        cEntity.setRideRequest(entity);
                        return cEntity;
                    })
                    .collect(Collectors.toList());
            entity.setCandidates(candidates);
        }
        return entity;
    }

    public static RideRequest toDomain(RideRequestPersistenceEntity entity) {
        if (entity == null) return null;
        List<RideCandidate> candidates = new ArrayList<>();
        if (entity.getCandidates() != null) {
            candidates = entity.getCandidates().stream()
                    .map(c -> new RideCandidate(
                            UUID.fromString(c.getCandidateId()),
                            UUID.fromString(c.getRideRequest().getRideRequestId()),
                            UUID.fromString(c.getDriverId()),
                            c.getDriverName(),
                            c.getVehicleType(),
                            c.getRatingAverage(),
                            c.getPhotoUrl(),
                            c.getStatus(),
                            c.getCreatedAt() != null ? c.getCreatedAt().toInstant() : Instant.now()
                    ))
                    .collect(Collectors.toList());
        }

        return new RideRequest(
                UUID.fromString(entity.getRideRequestId()),
                UUID.fromString(entity.getPassengerId()),
                entity.getSelectedDriverId() != null ? UUID.fromString(entity.getSelectedDriverId()) : null,
                entity.getOrigin(),
                entity.getDestination(),
                entity.getDistanceKm(),
                entity.getEstimatedFare(),
                entity.getStatus(),
                entity.isExpired(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : Instant.now(),
                candidates
        );
    }
}
