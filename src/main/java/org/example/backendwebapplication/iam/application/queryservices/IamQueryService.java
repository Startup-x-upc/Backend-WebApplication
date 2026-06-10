package org.example.backendwebapplication.iam.application.queryservices;

import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;
import org.example.backendwebapplication.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetAllProfilesQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetProfileByAccountIdQuery;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;

import java.util.List;

/**
 * Application service interface for IAM read operations.
 *
 * <p>Declares the use cases related to account and profile queries.
 * Implementation lives in {@code application.internal.queryservices}.</p>
 */
public interface IamQueryService {

    /**
     * Retrieves an account by its unique identifier.
     *
     * @param query the query containing the account ID
     * @return the Account or a NOT_FOUND ApplicationError
     */
    Result<Account, ApplicationError> handle(GetAccountByIdQuery query);

    /**
     * Retrieves a user profile by its associated account ID.
     *
     * @param query the query containing the account ID
     * @return the UserProfile or a NOT_FOUND ApplicationError
     */
    Result<UserProfile, ApplicationError> handle(GetProfileByAccountIdQuery query);

    /**
     * Retrieves all user profiles in the system.
     *
     * @param query the query (no parameters needed)
     * @return list of all UserProfiles
     */
    List<UserProfile> handle(GetAllProfilesQuery query);
}
