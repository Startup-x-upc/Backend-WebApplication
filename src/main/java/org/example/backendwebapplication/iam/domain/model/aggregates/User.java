package org.example.backendwebapplication.iam.domain.model.aggregates;

import org.example.backendwebapplication.iam.domain.model.events.DriverRegisteredEvent;
import org.example.backendwebapplication.iam.domain.model.events.ProfileUpdatedEvent;
import org.example.backendwebapplication.iam.domain.model.events.UserRegisteredEvent;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.model.valueobjects.PasswordHash;
import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.UUID;

/**
 * User aggregate root.
 * <p>Represents a registered user credential in the system. Holds the
 * authentication identity (email, password hash, role). Profile information
 * lives in the separate {@link org.example.backendwebapplication.iam.domain.model.entities.Profile} entity.</p>
 *
 * <p>Uses a double-identifier pattern: {@code userId} is the business
 * identity (UUID), while the persistence layer uses its own {@code Long id}
 * from {@code AuditableAbstractPersistenceEntity}.</p>
 */
public class User extends AbstractDomainAggregateRoot<User> {

    private UUID userId;
    private Email email;
    private PasswordHash passwordHash;
    private UserRole role;
    private Instant createdAt;
    private Instant updatedAt;

    /** JPA-friendly constructor. */
    User() {
    }

    /**
     * Creates a new User from registration data.
     */
    public User(Email email, PasswordHash passwordHash, UserRole role) {
        this.userId = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Full reconstruction constructor (used by persistence assembler).
     */
    public User(UUID userId, Email email, PasswordHash passwordHash, UserRole role,
                Instant createdAt, Instant updatedAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public UUID getUserId()            { return userId; }
    public String getEmail()           { return email.address(); }
    public String getPasswordHash()    { return passwordHash.hash(); }
    public UserRole getRole()          { return role; }
    public Instant getCreatedAt()      { return createdAt; }
    public Instant getUpdatedAt()      { return updatedAt; }

    // ── Domain behaviour ─────────────────────────────────────────────────

    public boolean passwordMatches(String plainText) {
        return passwordHash.matches(plainText);
    }

    public void onPassengerRegistered(String fullName) {
        registerDomainEvent(new UserRegisteredEvent(
                userId, email.address(), role.name(), fullName));
    }

    public void onDriverRegistered(String fullName, String vehicleType,
                                   String licenseNumber, String soatNumber) {
        registerDomainEvent(new DriverRegisteredEvent(
                userId, email.address(), fullName,
                vehicleType, licenseNumber, soatNumber));
    }

    /**
     * Signals that the profile belonging to this user has been updated.
     * Registers a {@link ProfileUpdatedEvent} so other bounded contexts
     * (e.g. Driver Management) can sync the display data.
     */
    public void onProfileUpdated(UUID profileId, String fullName, String photoUrl) {
        registerDomainEvent(new ProfileUpdatedEvent(
                userId, profileId, fullName, photoUrl));
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
