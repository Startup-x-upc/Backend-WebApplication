package org.example.backendwebapplication.trustreputation.application.commandservices;

import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;
import org.example.backendwebapplication.trustreputation.domain.model.commands.SubmitDriverRatingCommand;
import org.example.backendwebapplication.trustreputation.domain.model.commands.SubmitPassengerRatingCommand;

import java.util.UUID;

public interface TripRatingCommandService {
    TripRating handle(SubmitDriverRatingCommand command);
    TripRating handle(SubmitPassengerRatingCommand command);
    TripRating handleSkipDriverRating(UUID tripId);
    TripRating handleSkipPassengerRating(UUID tripId);
    TripRating handleCreateTripRating(UUID tripId, UUID driverId, UUID passengerId);
}
