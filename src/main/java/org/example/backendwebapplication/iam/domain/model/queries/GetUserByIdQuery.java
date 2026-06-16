package org.example.backendwebapplication.iam.domain.model.queries;

import java.util.UUID;

/**
 * Query to retrieve a User by its business identifier.
 *
 * @param userId the user's UUID
 */
public record GetUserByIdQuery(UUID userId) {
}
