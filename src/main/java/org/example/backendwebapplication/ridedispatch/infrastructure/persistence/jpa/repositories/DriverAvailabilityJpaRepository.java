package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.DriverAvailabilityPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverAvailabilityJpaRepository extends JpaRepository<DriverAvailabilityPersistenceEntity, Long> {
    Optional<DriverAvailabilityPersistenceEntity> findByDriverAvailabilityId(String driverAvailabilityId);
    Optional<DriverAvailabilityPersistenceEntity> findByDriverId(String driverId);
}
