package org.example.backendwebapplication.iam.domain.model.commands;

/**
 * Command to authenticate an existing user.
 *
 * @param email    the user's email address
 * @param password the raw password to verify
 */
public record LoginCommand(String email, String password) {
}
