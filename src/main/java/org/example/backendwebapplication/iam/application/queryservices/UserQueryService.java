package org.example.backendwebapplication.iam.application.queryservices;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.queries.GetUserByEmailQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetUserByIdQuery;

import java.util.Optional;

/**
 * Application service interface for User read operations.
 * <p>Implementation lives in {@code application.internal.queryservices}.</p>
 */
public interface UserQueryService {

    /**
     * Retrieves a User by their business identifier.
     *
     * @param query the query containing the user UUID
     * @return the User, or {@code Optional.empty()} if not found
     */
    Optional<User> handle(GetUserByIdQuery query);

    /**
     * Retrieves a User by their email address.
     *
     * @param query the query containing the email
     * @return the User, or {@code Optional.empty()} if not found
     */
    Optional<User> handle(GetUserByEmailQuery query);
}
