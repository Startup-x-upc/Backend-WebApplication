package org.example.backendwebapplication.iam.domain.model.valueobjects;

/**
 * HashedPassword value object.
 * Wraps a BCrypt-hashed password string, ensuring it is never blank.
 *
 * @param hash the BCrypt hash of the user's password
 */
public record HashedPassword(String hash) {

    public HashedPassword {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be null or blank");
        }
    }
}
