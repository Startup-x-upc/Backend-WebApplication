package org.example.backendwebapplication.ridedispatch.application.queryservices;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface RideQueryService {
    List<RideRequest> handle(GetOpenRideRequestsQuery query);
    Optional<RideRequest> handle(GetRideRequestByIdQuery query);
    List<RideCandidate> handle(GetCandidatesForRequestQuery query);
    Optional<RideCandidate> handle(GetDriverActiveCandidateQuery query);
    Optional<Ride> handle(GetActiveRideForDriverQuery query);
    Optional<Ride> handle(GetRideByIdQuery query);
    List<Ride> handle(GetPassengerTripHistoryQuery query);
    long count(GetPassengerTripHistoryQuery query);
    List<Ride> handle(GetDriverTripHistoryQuery query);
    long count(GetDriverTripHistoryQuery query);
    Optional<DriverAvailability> handle(GetDriverAvailabilityQuery query);
}
