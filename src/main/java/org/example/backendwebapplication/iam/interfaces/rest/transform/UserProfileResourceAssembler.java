package org.example.backendwebapplication.iam.interfaces.rest.transform;

import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;
import org.example.backendwebapplication.iam.interfaces.rest.resources.UserProfileResource;

/**
 * Assembler that converts {@link UserProfile} domain aggregates
 * to REST response resources.
 */
public final class UserProfileResourceAssembler {

    private UserProfileResourceAssembler() {}

    public static UserProfileResource toResourceFromDomain(UserProfile profile) {
        return new UserProfileResource(
                profile.getId(),
                profile.getAccountId(),
                profile.getFullName(),
                profile.getEmail(),
                profile.getPhotoUrl());
    }
}
