package org.example.backendwebapplication.iam.domain.repositories;

import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for {@link UserProfile} persistence operations.
 * Implemented in the infrastructure layer.
 */
public interface UserProfileRepository {

    Optional<UserProfile> findById(Long id);

    Optional<UserProfile> findByAccountId(Long accountId);

    List<UserProfile> findAll();

    UserProfile save(UserProfile profile);
}
