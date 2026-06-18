package org.example.backendwebapplication.iam.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Email value object.
 * <p>Encapsulates and validates an email address string.
 * The validation regex is intentionally simple to avoid false negatives
 * while still catching obvious formatting mistakes.</p>
 *
 * @param address the raw email string
 */
public record Email(String address) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private static final int MAX_LENGTH = 255;

    public Email {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }
        if (address.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email address must not exceed " + MAX_LENGTH + " characters");
        }
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + address);
        }
    }
}
