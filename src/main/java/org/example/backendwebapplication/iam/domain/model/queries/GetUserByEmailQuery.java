package org.example.backendwebapplication.iam.domain.model.queries;

/**
 * Query to retrieve a User by their email address.
 *
 * @param email the user's email
 */
public record GetUserByEmailQuery(String email) {
}
