package org.example.backendwebapplication.ridedispatch.domain.repositories;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RideRepository {

    Ride save(Ride ride);

    Optional<Ride> findById(UUID id);

    Optional<Ride> findActiveRideByDriverId(UUID driverId);

    List<Ride> findByPassengerId(UUID passengerId, int page, int perPage, RideStatus status);

    long countByPassengerId(UUID passengerId, RideStatus status);

    List<Ride> findByDriverId(UUID driverId, int page, int perPage, RideStatus status);

    long countByDriverId(UUID driverId, RideStatus status);
}
