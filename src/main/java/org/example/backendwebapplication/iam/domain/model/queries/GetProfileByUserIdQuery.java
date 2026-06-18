package org.example.backendwebapplication.iam.domain.model.queries;

import java.util.UUID;

/**
 * Query to retrieve a Profile by its associated User ID.
 *
 * @param userId the user's UUID
 */
public record GetProfileByUserIdQuery(UUID userId) {
}
