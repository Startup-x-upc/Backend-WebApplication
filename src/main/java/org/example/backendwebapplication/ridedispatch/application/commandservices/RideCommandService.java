package org.example.backendwebapplication.ridedispatch.application.commandservices;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.domain.model.commands.*;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;

public interface RideCommandService {
    Result<RideRequest, ApplicationError> handle(CreateRideRequestCommand command);
    Result<RideCandidate, ApplicationError> handle(ApplyAsCandidateCommand command);
    Result<Ride, ApplicationError> handle(SelectCandidateCommand command);
    Result<Ride, ApplicationError> handle(AdvanceRideStatusCommand command);
    Result<Ride, ApplicationError> handle(CancelRideCommand command);
}
