package org.example.backendwebapplication.iam.domain.model.valueobjects;

/**
 * Represents the role of a registered user in the system.
 * <p>Once assigned at registration, a role cannot be changed (BR3).</p>
 */
public enum UserRole {
    PASSENGER,
    DRIVER,
    ADMIN
}
