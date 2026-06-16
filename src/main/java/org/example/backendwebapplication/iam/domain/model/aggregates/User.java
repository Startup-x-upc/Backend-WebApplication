package org.example.backendwebapplication.iam.domain.model.aggregates;

import org.example.backendwebapplication.iam.domain.model.events.DriverRegisteredEvent;
import org.example.backendwebapplication.iam.domain.model.events.UserRegisteredEvent;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.model.valueobjects.PasswordHash;
import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.util.UUID;

/**
 * User aggregate root.
 * <p>Represents a registered user credential in the system. Holds the
 * authentication identity (email, password hash, role). Profile information
 * lives in the separate {@link org.example.backendwebapplication.iam.domain.model.entities.Profile} entity.</p>
 *
 * <p>Uses a double-identifier pattern: {@code userId} is the business
 * identity (UUID), while the persistence layer uses its own {@code Long id}
 * from {@link AuditableAbstractPersistenceEntity}.</p>
 */
public class User extends AbstractDomainAggregateRoot<User> {

    private UUID userId;
    private Email email;
    private PasswordHash passwordHash;
    private UserRole role;

    /** JPA-friendly constructor. */
    User() {
    }

    /**
     * Creates a new User from registration data.
     *
     * @param email        the user's email address
     * @param passwordHash the already-hashed password
     * @param role         the assigned role
     */
    public User(Email email, PasswordHash passwordHash, UserRole role) {
        this.userId = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Full reconstruction constructor (used by persistence assembler).
     */
    public User(UUID userId, Email email, PasswordHash passwordHash, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email.address();
    }

    public String getPasswordHash() {
        return passwordHash.hash();
    }

    public UserRole getRole() {
        return role;
    }

    // ── Domain behaviour ─────────────────────────────────────────────────

    /**
     * Verifies whether the given plain-text password matches this user's hash.
     */
    public boolean passwordMatches(String plainText) {
        return passwordHash.matches(plainText);
    }

    /**
     * Signals that this user has just been registered (passenger flow).
     * Registers a {@link UserRegisteredEvent}.
     */
    public void onPassengerRegistered(String fullName) {
        registerDomainEvent(new UserRegisteredEvent(
                userId, email.address(), role.name(), fullName));
    }

    /**
     * Signals that this user has just been registered as a driver.
     * Registers a {@link DriverRegisteredEvent} so Driver Management and
     * Monetization can create their own entities.
     */
    public void onDriverRegistered(String fullName, String vehicleType,
                                   String licenseNumber, String soatNumber) {
        registerDomainEvent(new DriverRegisteredEvent(
                userId, email.address(), fullName,
                vehicleType, licenseNumber, soatNumber));
    }

    // ── Object contracts ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        return "User{userId=%s, email=%s, role=%s}".formatted(userId, email.address(), role);
    }
}
