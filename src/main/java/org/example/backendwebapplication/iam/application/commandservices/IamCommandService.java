package org.example.backendwebapplication.iam.application.commandservices;

import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;
import org.example.backendwebapplication.iam.domain.model.commands.SignInCommand;
import org.example.backendwebapplication.iam.domain.model.commands.SignUpCommand;
import org.example.backendwebapplication.iam.domain.model.commands.UpdateUserProfileCommand;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;

/**
 * Application service interface for IAM write operations.
 *
 * <p>Declares the use cases related to account and profile mutations.
 * Implementation lives in {@code application.internal.commandservices}.</p>
 */
public interface IamCommandService {

    /**
     * Registers a new user account and auto-creates the associated profile.
     *
     * @param command the sign-up command
     * @return the created Account or an ApplicationError
     */
    Result<Account, ApplicationError> handle(SignUpCommand command);

    /**
     * Authenticates an existing user by verifying credentials.
     *
     * @param command the sign-in command
     * @return the authenticated Account or an ApplicationError
     */
    Result<Account, ApplicationError> handle(SignInCommand command);

    /**
     * Updates an existing user profile.
     *
     * @param command the update profile command
     * @return the updated UserProfile or an ApplicationError
     */
    Result<UserProfile, ApplicationError> handle(UpdateUserProfileCommand command);
}
