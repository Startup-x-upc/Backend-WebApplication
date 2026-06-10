package org.example.backendwebapplication.iam.domain.model.valueobjects;

/**
 * FullName value object.
 * Represents the full display name of a user.
 *
 * @param value the full name string
 */
public record FullName(String value) {

    public FullName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Full name must not be null or blank");
        }
    }
}
