package org.example.backendwebapplication.iam.interfaces.rest.resources;

import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;

/**
 * REST resource returned after successful authentication or registration.
 *
 * @param id    the account identifier
 * @param email the account email
 * @param role  the account role
 */
public record AuthenticatedAccountResource(Long id, String email, UserRole role) {
}
