package org.example.backendwebapplication.ridedispatch.interfaces.rest.transform;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.interfaces.rest.resources.DriverAvailabilityResponse;
import org.example.backendwebapplication.ridedispatch.interfaces.rest.resources.RideCandidateResponse;
import org.example.backendwebapplication.ridedispatch.interfaces.rest.resources.RideRequestResponse;
import org.example.backendwebapplication.ridedispatch.interfaces.rest.resources.RideResponse;

public class RideResourceAssembler {

    public static RideRequestResponse toResource(RideRequest domain, String passengerName, String passengerPhotoUrl) {
        if (domain == null) return null;
        return new RideRequestResponse(
                domain.getId(),
                domain.getPassengerId(),
                passengerName,
                passengerPhotoUrl,
                domain.getOrigin(),
                domain.getDestination(),
                domain.getDistanceKm(),
                domain.getEstimatedFare(),
                domain.getStatus().name(),
                domain.isExpired(),
                domain.getCreatedAt() != null ? domain.getCreatedAt().toString() : null
        );
    }

    public static RideCandidateResponse toResource(RideCandidate domain) {
        if (domain == null) return null;
        return new RideCandidateResponse(
                domain.getId(),
                domain.getRequestId(),
                domain.getDriverId(),
                domain.getDriverName(),
                domain.getVehicleType(),
                domain.getRatingAverage(),
                domain.getPhotoUrl(),
                domain.getStatus().name(),
                domain.getAppliedAt() != null ? domain.getAppliedAt().toString() : null
        );
    }

    public static RideResponse toResource(Ride domain, String passengerName, String driverName) {
        if (domain == null) return null;
        return new RideResponse(
                domain.getId(),
                domain.getRequestId(),
                domain.getPassengerId(),
                domain.getDriverId(),
                driverName,
                passengerName,
                domain.getOrigin(),
                domain.getDestination(),
                domain.getEstimatedFare(),
                domain.getStatus().name(),
                domain.getCreatedAt() != null ? domain.getCreatedAt().toString() : null,
                domain.getCompletedAt() != null ? domain.getCompletedAt().toString() : null
        );
    }

    public static DriverAvailabilityResponse toResource(DriverAvailability domain) {
        if (domain == null) return null;
        return new DriverAvailabilityResponse(
                domain.getId(),
                domain.getDriverId(),
                domain.isAvailable(),
                domain.isBusy(),
                domain.getActiveRideId(),
                domain.getLatitude(),
                domain.getLongitude()
        );
    }
}
