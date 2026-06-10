package org.example.backendwebapplication.iam.domain.model.valueobjects;

import jakarta.validation.constraints.Email;

/**
 * EmailAddress value object.
 * Encapsulates and validates an email address string.
 *
 * @param address the raw email string
 */
public record EmailAddress(@Email String address) {

    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }
    }

    public String getAddress() { return address; }
}
