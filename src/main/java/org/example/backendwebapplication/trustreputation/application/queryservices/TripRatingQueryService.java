package org.example.backendwebapplication.trustreputation.application.queryservices;

import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetDriverReputationQuery;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetPassengerReputationQuery;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetTripRatingByTripIdQuery;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.DriverReputation;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.PassengerReputation;

import java.util.Optional;

public interface TripRatingQueryService {
    Optional<TripRating> handle(GetTripRatingByTripIdQuery query);
    DriverReputation handle(GetDriverReputationQuery query);
    PassengerReputation handle(GetPassengerReputationQuery query);
}
