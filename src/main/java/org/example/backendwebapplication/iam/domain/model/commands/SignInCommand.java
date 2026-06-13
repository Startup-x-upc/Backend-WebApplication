package org.example.backendwebapplication.iam.domain.model.commands;

/**
 * Command to authenticate an existing user account.
 *
 * @param email    the user's email address
 * @param password the raw password to verify
 */
public record SignInCommand(String email, String password) {
}
