package org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;
import org.example.backendwebapplication.trustreputation.domain.repositories.TripRatingRepository;
import org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.assemblers.TripRatingPersistenceAssembler;
import org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.entities.TripRatingPersistenceEntity;
import org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.repositories.TripRatingJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TripRatingRepositoryImpl implements TripRatingRepository {

    private final TripRatingJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TripRatingRepositoryImpl(TripRatingJpaRepository jpaRepository, ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TripRating save(TripRating rating) {
        var existing = jpaRepository.findByRatingId(rating.getId().toString()).orElse(null);
        TripRatingPersistenceEntity entity = TripRatingPersistenceAssembler.toEntity(rating);
        if (existing != null) {
            entity.setId(existing.getId());
        }
        TripRatingPersistenceEntity saved = jpaRepository.save(entity);

        rating.domainEvents().forEach(eventPublisher::publishEvent);
        rating.clearDomainEvents();

        return TripRatingPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<TripRating> findByTripId(UUID tripId) {
        return jpaRepository.findByTripId(tripId.toString())
                .map(TripRatingPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<TripRating> findById(UUID id) {
        return jpaRepository.findByRatingId(id.toString())
                .map(TripRatingPersistenceAssembler::toDomain);
    }

    @Override
    public Double findAverageDriverScore(UUID driverId) {
        return jpaRepository.findAverageDriverScore(driverId.toString());
    }

    @Override
    public Long countDriverRatings(UUID driverId) {
        return jpaRepository.countDriverRatings(driverId.toString());
    }

    @Override
    public Double findAveragePassengerScore(UUID passengerId) {
        return jpaRepository.findAveragePassengerScore(passengerId.toString());
    }

    @Override
    public Long countPassengerRatings(UUID passengerId) {
        return jpaRepository.countPassengerRatings(passengerId.toString());
    }
}
