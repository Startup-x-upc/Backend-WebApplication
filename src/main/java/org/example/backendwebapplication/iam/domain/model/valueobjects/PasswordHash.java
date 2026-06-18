package org.example.backendwebapplication.iam.domain.model.valueobjects;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

/**
 * PasswordHash value object.
 * <p>Receives the plain-text password in its constructor and applies bcrypt
 * hashing internally via {@link BCryptPasswordEncoder}. The plain text is
 * never stored or exposed — only the resulting hash is retained.</p>
 *
 * <p>Two PasswordHash instances are equal when their hashes match.</p>
 */
public final class PasswordHash {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private final String hash;

    /**
     * Creates a new PasswordHash from a plain-text password.
     *
     * @param plainText the raw password (must not be null, blank, or shorter than 6 chars)
     * @throws IllegalArgumentException if the password is invalid
     */
    public PasswordHash(String plainText) {
        Objects.requireNonNull(plainText, "Password must not be null");
        if (plainText.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (plainText.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        this.hash = ENCODER.encode(plainText);
    }

    /**
     * Reconstructs a PasswordHash from an already-hashed value.
     * <p>Used by persistence assemblers when loading from the database.
     * Does NOT re-hash — the argument is stored as-is.</p>
     *
     * @param hash the bcrypt hash (must not be null or blank)
     * @throws IllegalArgumentException if the hash is null or blank
     */
    private PasswordHash(String hash, boolean alreadyHashed) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be null or blank");
        }
        this.hash = hash;
    }

    /**
     * Factory method to reconstruct a PasswordHash from a persisted hash.
     * Does NOT validate length or apply bcrypt — trusts the stored value.
     *
     * @param hash the bcrypt hash string from the database
     * @return a PasswordHash wrapping the given hash
     */
    public static PasswordHash fromHash(String hash) {
        return new PasswordHash(hash, true);
    }

    /**
     * @return the bcrypt hash string (for persistence)
     */
    public String hash() {
        return hash;
    }

    /**
     * Verifies whether the given plain-text password matches this hash.
     *
     * @param plainText the raw password to check
     * @return {@code true} if the password matches
     */
    public boolean matches(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return false;
        }
        return ENCODER.matches(plainText, hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordHash that)) return false;
        return hash.equals(that.hash);
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    @Override
    public String toString() {
        return "PasswordHash{*****}";
    }
}
