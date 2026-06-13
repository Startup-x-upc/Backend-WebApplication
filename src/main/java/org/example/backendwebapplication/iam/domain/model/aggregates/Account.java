package org.example.backendwebapplication.iam.domain.model.aggregates;

import org.example.backendwebapplication.iam.domain.model.commands.SignUpCommand;
import org.example.backendwebapplication.iam.domain.model.events.AccountCreatedEvent;
import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.domain.model.valueobjects.HashedPassword;
import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Account aggregate root.
 *
 * <p>Represents a registered user credential in the system.
 * Holds the email, hashed password and role.
 * Profile information is kept in the separate {@link UserProfile} aggregate.</p>
 */
public class Account extends AbstractDomainAggregateRoot<Account> {

    @Getter
    @Setter
    private Long id;

    private EmailAddress email;

    private HashedPassword password;

    @Getter
    private UserRole role;

    /**
     * Creates a new account from a SignUpCommand with the already-hashed password.
     */
    public Account(SignUpCommand command, String hashedPassword) {
        this.email    = new EmailAddress(Objects.requireNonNull(command.email(), "email must not be null"));
        this.password = new HashedPassword(hashedPassword);
        this.role     = Objects.requireNonNull(command.role(), "role must not be null");
    }

    /**
     * Reconstructs an Account from persisted values (used by assembler).
     */
    public Account(Long id, String email, String hashedPassword, UserRole role) {
        this.id       = id;
        this.email    = new EmailAddress(email);
        this.password = new HashedPassword(hashedPassword);
        this.role     = role;
    }

    /** @return the raw email string */
    public String getEmail() { return email.address(); }

    /** @return the BCrypt password hash */
    public String getPasswordHash() { return password.hash(); }

    /**
     * Signals that this account has just been created and persisted.
     * Registers an {@link AccountCreatedEvent} for interested subscribers.
     */
    public void onCreated() {
        registerDomainEvent(AccountCreatedEvent.from(this));
    }
}
