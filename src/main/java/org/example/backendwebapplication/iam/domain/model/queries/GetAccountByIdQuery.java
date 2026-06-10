package org.example.backendwebapplication.iam.domain.model.queries;

/**
 * Query to retrieve an account by its unique identifier.
 *
 * @param id the account identifier
 */
public record GetAccountByIdQuery(Long id) {
}
