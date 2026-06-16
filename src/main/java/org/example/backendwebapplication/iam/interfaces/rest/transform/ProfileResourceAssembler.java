package org.example.backendwebapplication.iam.interfaces.rest.transform;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.interfaces.rest.resources.ProfileResource;

/**
 * Stateless assembler that converts domain Profile and User aggregates
 * into {@link ProfileResource} REST responses.
 * <p>Composes the read model by merging Profile data with User's email and role.</p>
 */
public final class ProfileResourceAssembler {

    private ProfileResourceAssembler() {}

    /**
     * Creates a composed read model from both Profile and User.
     */
    public static ProfileResource toResource(Profile profile, User user) {
        return new ProfileResource(
                profile.getProfileId(),
                profile.getUserId(),
                user.getEmail(),
                profile.getFullName(),
                user.getRole().name(),
                profile.getPhotoUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }

    /**
     * Creates a resource from Profile only (no User data composed).
     * Email and role will be {@code null}.
     */
    public static ProfileResource toResource(Profile profile) {
        return new ProfileResource(
                profile.getProfileId(),
                profile.getUserId(),
                null,
                profile.getFullName(),
                null,
                profile.getPhotoUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }
}
