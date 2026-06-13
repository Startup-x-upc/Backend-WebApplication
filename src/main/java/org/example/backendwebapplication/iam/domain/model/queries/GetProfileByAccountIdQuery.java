package org.example.backendwebapplication.iam.domain.model.queries;

/**
 * Query to retrieve a user profile by its associated account ID.
 *
 * @param accountId the account identifier
 */
public record GetProfileByAccountIdQuery(Long accountId) {
}
