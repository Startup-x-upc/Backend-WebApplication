package org.example.backendwebapplication.iam.domain.model.commands;

/**
 * Command to register a new PASSENGER user.
 * <p>Creates a User + Profile in the same transaction.</p>
 *
 * @param email    the user's email address
 * @param password the raw (unhashed) password
 * @param fullName the user's display name
 */
public record RegisterPassengerCommand(
        String email,
        String password,
        String fullName) {
}
