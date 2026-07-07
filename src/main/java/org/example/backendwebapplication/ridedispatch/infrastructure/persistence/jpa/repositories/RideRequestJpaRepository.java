package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RideRequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRequestJpaRepository extends JpaRepository<RideRequestPersistenceEntity, Long> {
    Optional<RideRequestPersistenceEntity> findByRideRequestId(String rideRequestId);
    Optional<RideRequestPersistenceEntity> findFirstByPassengerIdAndStatusAndIsExpiredFalse(String passengerId, RideStatus status);
    List<RideRequestPersistenceEntity> findAllByStatus(RideStatus status);
}
