package org.example.backendwebapplication.trustreputation.interfaces.rest.transform;

import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.DriverReputation;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.PassengerReputation;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.DriverReputationResponse;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.PassengerReputationResponse;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.TripRatingResponse;

public class TripRatingResourceAssembler {

    public static TripRatingResponse toResource(TripRating domain) {
        if (domain == null) return null;
        return new TripRatingResponse(
                domain.getId(),
                domain.getTripId(),
                domain.getDriverId(),
                domain.getPassengerId(),
                domain.getDriverRatingStatus().name(),
                domain.getPassengerRatingStatus().name(),
                domain.getDriverScore(),
                domain.getPassengerScore(),
                domain.getPassengerComment(),
                domain.getRateableUntil() != null ? domain.getRateableUntil().toString() : null,
                domain.getCreatedAt() != null ? domain.getCreatedAt().toString() : null
        );
    }

    public static DriverReputationResponse toResource(DriverReputation domain) {
        if (domain == null) return null;
        return new DriverReputationResponse(
                domain.driverId(),
                domain.averageScore(),
                domain.totalRatings()
        );
    }

    public static PassengerReputationResponse toResource(PassengerReputation domain) {
        if (domain == null) return null;
        return new PassengerReputationResponse(
                domain.passengerId(),
                domain.averageScore(),
                domain.totalRatings()
        );
    }
}
