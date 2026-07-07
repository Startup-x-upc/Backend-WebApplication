package org.example.backendwebapplication.trustreputation.domain.repositories;

import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;

import java.util.Optional;
import java.util.UUID;

public interface TripRatingRepository {
    TripRating save(TripRating tripRating);
    Optional<TripRating> findByTripId(UUID tripId);
    Optional<TripRating> findById(UUID id);
    Double findAverageDriverScore(UUID driverId);
    Long countDriverRatings(UUID driverId);
    Double findAveragePassengerScore(UUID passengerId);
    Long countPassengerRatings(UUID passengerId);
}
