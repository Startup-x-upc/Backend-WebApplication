package org.example.backendwebapplication.iam.application.commandservices;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.commands.LoginCommand;
import org.example.backendwebapplication.iam.domain.model.commands.RegisterDriverCommand;
import org.example.backendwebapplication.iam.domain.model.commands.RegisterPassengerCommand;
import org.example.backendwebapplication.iam.domain.model.commands.UpdateProfileCommand;
import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;

/**
 * Application service interface for IAM write operations.
 * <p>Implementation lives in {@code application.internal.commandservices}.</p>
 */
public interface UserCommandService {

    /**
     * Registers a new PASSENGER user. Creates User + Profile in the same transaction.
     *
     * @param command the registration command
     * @return the created User, or an ApplicationError
     */
    Result<User, ApplicationError> handle(RegisterPassengerCommand command);

    /**
     * Registers a new DRIVER user. Creates User + Profile, then emits
     * {@code DriverRegisteredEvent} for other bounded contexts.
     *
     * @param command the registration command
     * @return the created User, or an ApplicationError
     */
    Result<User, ApplicationError> handle(RegisterDriverCommand command);

    /**
     * Authenticates an existing user by verifying credentials.
     *
     * @param command the login command
     * @return the authenticated User, or an ApplicationError
     */
    Result<User, ApplicationError> handle(LoginCommand command);

    /**
     * Updates an existing user profile.
     *
     * @param command the update profile command
     * @return the updated Profile, or an ApplicationError
     */
    Result<Profile, ApplicationError> handle(UpdateProfileCommand command);
}
