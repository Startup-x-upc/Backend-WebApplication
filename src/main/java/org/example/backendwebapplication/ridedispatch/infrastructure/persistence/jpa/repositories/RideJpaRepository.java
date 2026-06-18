package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RidePersistenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RideJpaRepository extends JpaRepository<RidePersistenceEntity, Long> {
    Optional<RidePersistenceEntity> findByRideId(String rideId);
    Optional<RidePersistenceEntity> findByDriverIdAndStatusNotIn(String driverId, Collection<RideStatus> statuses);
    Page<RidePersistenceEntity> findByPassengerId(String passengerId, Pageable pageable);
    Page<RidePersistenceEntity> findByPassengerIdAndStatus(String passengerId, RideStatus status, Pageable pageable);
    long countByPassengerId(String passengerId);
    long countByPassengerIdAndStatus(String passengerId, RideStatus status);
    Page<RidePersistenceEntity> findByDriverId(String driverId, Pageable pageable);
    Page<RidePersistenceEntity> findByDriverIdAndStatus(String driverId, RideStatus status, Pageable pageable);
    long countByDriverId(String driverId);
    long countByDriverIdAndStatus(String driverId, RideStatus status);
}
