package org.example.backendwebapplication.iam.interfaces.rest.transform;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.interfaces.rest.resources.MyProfileResource;
import org.example.backendwebapplication.iam.interfaces.rest.resources.ProfileResource;

/**
 * Stateless assembler that converts domain Profile entities
 * into REST response resources.
 */
public final class ProfileResourceAssembler {

    private ProfileResourceAssembler() {}

    /**
     * Creates a clean ProfileResource (no email/role).
     */
    public static ProfileResource toResource(Profile profile) {
        return new ProfileResource(
                profile.getProfileId(),
                profile.getUserId(),
                profile.getFullName(),
                profile.getPhotoUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }

    /**
     * Creates a composed read model for {@code GET /users/me/profile},
     * merging Profile data with User's email and role.
     */
    public static MyProfileResource toMyProfileResource(Profile profile, User user) {
        return new MyProfileResource(
                profile.getProfileId(),
                profile.getUserId(),
                user.getEmail(),
                profile.getFullName(),
                user.getRole().name(),
                profile.getPhotoUrl(),
                profile.getCreatedAt());
    }
}
