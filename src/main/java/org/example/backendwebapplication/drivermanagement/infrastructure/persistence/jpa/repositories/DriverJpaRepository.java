package org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;
import org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverJpaRepository extends JpaRepository<DriverPersistenceEntity, Long> {
    Optional<DriverPersistenceEntity> findByDriverId(String driverId);
    Optional<DriverPersistenceEntity> findByUserId(String userId);
    Page<DriverPersistenceEntity> findByAccessStatus(DriverAccessStatus accessStatus, Pageable pageable);
    long countByAccessStatus(DriverAccessStatus accessStatus);
}
