package org.example.backendwebapplication.ridedispatch.domain.repositories;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RideRequestRepository {

    RideRequest save(RideRequest rideRequest);

    Optional<RideRequest> findById(UUID id);

    Optional<RideRequest> findOpenRequestByPassengerId(UUID passengerId);

    List<RideRequest> findAllByStatus(RideStatus status);
}
