package org.example.backendwebapplication.iam.interfaces.rest.transform;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.interfaces.rest.resources.UserResource;

/**
 * Stateless assembler that converts {@link User} domain aggregates
 * to {@link UserResource} REST responses.
 */
public final class UserResourceAssembler {

    private UserResourceAssembler() {}

    public static UserResource toResource(User user) {
        return new UserResource(
                user.getUserId(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt());
    }
}
