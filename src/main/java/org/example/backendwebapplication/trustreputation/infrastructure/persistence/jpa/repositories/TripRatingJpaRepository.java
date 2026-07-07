package org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.entities.TripRatingPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TripRatingJpaRepository extends JpaRepository<TripRatingPersistenceEntity, Long> {
    Optional<TripRatingPersistenceEntity> findByTripId(String tripId);
    Optional<TripRatingPersistenceEntity> findByRatingId(String ratingId);

    @Query("SELECT AVG(t.driverScore) FROM TripRatingPersistenceEntity t WHERE t.driverId = :driverId AND t.driverRatingStatus = 'RATED'")
    Double findAverageDriverScore(@Param("driverId") String driverId);

    @Query("SELECT COUNT(t) FROM TripRatingPersistenceEntity t WHERE t.driverId = :driverId AND t.driverRatingStatus = 'RATED'")
    Long countDriverRatings(@Param("driverId") String driverId);

    @Query("SELECT AVG(t.passengerScore) FROM TripRatingPersistenceEntity t WHERE t.passengerId = :passengerId AND t.passengerRatingStatus = 'RATED'")
    Double findAveragePassengerScore(@Param("passengerId") String passengerId);

    @Query("SELECT COUNT(t) FROM TripRatingPersistenceEntity t WHERE t.passengerId = :passengerId AND t.passengerRatingStatus = 'RATED'")
    Long countPassengerRatings(@Param("passengerId") String passengerId);
}
