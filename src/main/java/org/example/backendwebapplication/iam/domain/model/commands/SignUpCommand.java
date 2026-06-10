package org.example.backendwebapplication.iam.domain.model.commands;

import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;

/**
 * Command to register a new user account in the system.
 *
 * @param email    the user's email address
 * @param password the raw (unhashed) password
 * @param role     the user's role (PASSENGER or DRIVER)
 * @param fullName the user's display name
 * @param photoUrl optional profile photo URL
 */
public record SignUpCommand(
        String email,
        String password,
        UserRole role,
        String fullName,
        String photoUrl) {
}
