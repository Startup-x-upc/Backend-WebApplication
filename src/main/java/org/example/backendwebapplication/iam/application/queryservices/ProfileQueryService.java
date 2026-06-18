package org.example.backendwebapplication.iam.application.queryservices;

import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.domain.model.queries.GetProfileByUserIdQuery;

import java.util.Optional;

/**
 * Application service interface for Profile read operations.
 * <p>Implementation lives in {@code application.internal.queryservices}.</p>
 */
public interface ProfileQueryService {

    /**
     * Retrieves a Profile by its associated User ID.
     *
     * @param query the query containing the user UUID
     * @return the Profile, or {@code Optional.empty()} if not found
     */
    Optional<Profile> handle(GetProfileByUserIdQuery query);
}
