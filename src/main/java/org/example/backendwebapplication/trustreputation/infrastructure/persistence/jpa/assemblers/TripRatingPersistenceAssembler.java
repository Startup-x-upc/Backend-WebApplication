package org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;
import org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.entities.TripRatingPersistenceEntity;

import java.util.UUID;

public class TripRatingPersistenceAssembler {

    public static TripRatingPersistenceEntity toEntity(TripRating domain) {
        TripRatingPersistenceEntity entity = new TripRatingPersistenceEntity();
        entity.setRatingId(domain.getId().toString());
        entity.setTripId(domain.getTripId().toString());
        entity.setDriverId(domain.getDriverId().toString());
        entity.setPassengerId(domain.getPassengerId().toString());
        entity.setDriverRatingStatus(domain.getDriverRatingStatus());
        entity.setPassengerRatingStatus(domain.getPassengerRatingStatus());
        entity.setDriverScore(domain.getDriverScore());
        entity.setPassengerScore(domain.getPassengerScore());
        entity.setPassengerComment(domain.getPassengerComment());
        entity.setRateableUntil(domain.getRateableUntil());
        return entity;
    }

    public static TripRating toDomain(TripRatingPersistenceEntity entity) {
        TripRating domain = new TripRating();
        domain.setId(UUID.fromString(entity.getRatingId()));
        domain.setTripId(UUID.fromString(entity.getTripId()));
        domain.setDriverId(UUID.fromString(entity.getDriverId()));
        domain.setPassengerId(UUID.fromString(entity.getPassengerId()));
        domain.setDriverRatingStatus(entity.getDriverRatingStatus());
        domain.setPassengerRatingStatus(entity.getPassengerRatingStatus());
        domain.setDriverScore(entity.getDriverScore());
        domain.setPassengerScore(entity.getPassengerScore());
        domain.setPassengerComment(entity.getPassengerComment());
        domain.setRateableUntil(entity.getRateableUntil());
        domain.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : null);
        return domain;
    }
}
