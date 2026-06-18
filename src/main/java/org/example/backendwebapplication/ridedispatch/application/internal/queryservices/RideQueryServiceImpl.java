package org.example.backendwebapplication.ridedispatch.application.internal.queryservices;

import org.example.backendwebapplication.ridedispatch.application.queryservices.RideQueryService;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.domain.model.queries.*;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.CandidateStatus;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.domain.repositories.DriverAvailabilityRepository;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRepository;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRequestRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RideQueryServiceImpl implements RideQueryService {

    private final RideRequestRepository rideRequestRepository;
    private final RideRepository rideRepository;
    private final DriverAvailabilityRepository driverAvailabilityRepository;

    public RideQueryServiceImpl(RideRequestRepository rideRequestRepository,
                                 RideRepository rideRepository,
                                 DriverAvailabilityRepository driverAvailabilityRepository) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideRepository = rideRepository;
        this.driverAvailabilityRepository = driverAvailabilityRepository;
    }

    @Override
    public List<RideRequest> handle(GetOpenRideRequestsQuery query) {
        return rideRequestRepository.findAllByStatus(RideStatus.OPEN);
    }

    @Override
    public Optional<RideRequest> handle(GetRideRequestByIdQuery query) {
        return rideRequestRepository.findById(query.requestId());
    }

    @Override
    public List<RideCandidate> handle(GetCandidatesForRequestQuery query) {
        return rideRequestRepository.findById(query.requestId())
                .map(req -> {
                    if (!req.getPassengerId().equals(query.passengerId())) {
                        return Collections.<RideCandidate>emptyList();
                    }
                    return req.getCandidates();
                })
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<RideCandidate> handle(GetDriverActiveCandidateQuery query) {
        return rideRequestRepository.findAllByStatus(RideStatus.OPEN).stream()
                .flatMap(req -> req.getCandidates().stream())
                .filter(c -> c.getDriverId().equals(query.driverId()) && c.getStatus() == CandidateStatus.PROPOSED)
                .findFirst();
    }

    @Override
    public Optional<Ride> handle(GetActiveRideForDriverQuery query) {
        return rideRepository.findActiveRideByDriverId(query.driverId());
    }

    @Override
    public Optional<Ride> handle(GetRideByIdQuery query) {
        return rideRepository.findById(query.rideId());
    }

    @Override
    public List<Ride> handle(GetPassengerTripHistoryQuery query) {
        RideStatus statusEnum = null;
        if (query.status() != null && !query.status().trim().isEmpty()) {
            try {
                statusEnum = RideStatus.valueOf(query.status().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return rideRepository.findByPassengerId(query.passengerId(), query.page(), query.perPage(), statusEnum);
    }

    @Override
    public long count(GetPassengerTripHistoryQuery query) {
        RideStatus statusEnum = null;
        if (query.status() != null && !query.status().trim().isEmpty()) {
            try {
                statusEnum = RideStatus.valueOf(query.status().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return rideRepository.countByPassengerId(query.passengerId(), statusEnum);
    }

    @Override
    public List<Ride> handle(GetDriverTripHistoryQuery query) {
        RideStatus statusEnum = null;
        if (query.status() != null && !query.status().trim().isEmpty()) {
            try {
                statusEnum = RideStatus.valueOf(query.status().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return rideRepository.findByDriverId(query.driverId(), query.page(), query.perPage(), statusEnum);
    }

    @Override
    public long count(GetDriverTripHistoryQuery query) {
        RideStatus statusEnum = null;
        if (query.status() != null && !query.status().trim().isEmpty()) {
            try {
                statusEnum = RideStatus.valueOf(query.status().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return rideRepository.countByDriverId(query.driverId(), statusEnum);
    }

    @Override
    public Optional<DriverAvailability> handle(GetDriverAvailabilityQuery query) {
        return driverAvailabilityRepository.findByDriverId(query.driverId());
    }
}
