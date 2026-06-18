package org.example.backendwebapplication.drivermanagement.application.commandservices;

import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.*;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;

/**
 * Interface representing the driver command service.
 */
public interface DriverCommandService {

    Result<Driver, ApplicationError> handle(CreateDriverCommand command);

    Result<Driver, ApplicationError> handle(ToggleDriverAvailabilityCommand command);

    Result<Driver, ApplicationError> handle(RestrictDriverCommand command);

    Result<Driver, ApplicationError> handle(UnrestrictDriverCommand command);

    Result<Driver, ApplicationError> handle(UpdateDriverProfileCommand command);
}
