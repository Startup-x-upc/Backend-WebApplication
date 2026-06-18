package org.example.backendwebapplication.drivermanagement.domain.repositories;

import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DriverRepository port interface.
 */
public interface DriverRepository {

    Driver save(Driver driver);

    Optional<Driver> findByDriverId(UUID driverId);

    Optional<Driver> findByUserId(UUID userId);

    List<Driver> findAll(int page, int perPage, DriverAccessStatus accessStatus);

    long count(DriverAccessStatus accessStatus);
}
