package org.example.backendwebapplication.iam.domain.model.valueobjects;

/**
 * FullName value object.
 * <p>Represents the full display name of a user.
 * Enforces a minimum of 2 and a maximum of 100 characters.</p>
 *
 * @param value the full name string
 */
public record FullName(String value) {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public FullName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Full name must not be null or blank");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Full name must be at least " + MIN_LENGTH + " characters");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Full name must not exceed " + MAX_LENGTH + " characters");
        }
    }

    /**
     * Returns the trimmed value.
     */
    @Override
    public String value() {
        return value.trim();
    }
}
