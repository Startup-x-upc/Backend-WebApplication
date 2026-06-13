package org.example.backendwebapplication.iam.interfaces.rest.transform;

import org.example.backendwebapplication.iam.domain.model.commands.SignUpCommand;
import org.example.backendwebapplication.iam.interfaces.rest.resources.SignUpResource;

/**
 * Assembler that converts a {@link SignUpResource} REST resource
 * into a {@link SignUpCommand} domain command.
 */
public final class CreateAccountCommandFromResourceAssembler {

    private CreateAccountCommandFromResourceAssembler() {}

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
                resource.email(),
                resource.password(),
                resource.role(),
                resource.fullName(),
                resource.photoUrl());
    }
}
