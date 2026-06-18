package org.example.backendwebapplication.ridedispatch.domain.repositories;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;

import java.util.Optional;
import java.util.UUID;

public interface DriverAvailabilityRepository {

    DriverAvailability save(DriverAvailability driverAvailability);

    Optional<DriverAvailability> findByDriverId(UUID driverId);
}
