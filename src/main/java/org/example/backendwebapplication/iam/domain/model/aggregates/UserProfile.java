package org.example.backendwebapplication.iam.domain.model.aggregates;

import org.example.backendwebapplication.iam.domain.model.commands.UpdateUserProfileCommand;
import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.domain.model.valueobjects.FullName;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * UserProfile aggregate root.
 *
 * <p>Holds the display information of a registered user:
 * full name, email (denormalized for display), and photo URL.
 * Each profile is associated with exactly one {@link Account} via accountId.</p>
 */
public class UserProfile extends AbstractDomainAggregateRoot<UserProfile> {

    @Getter
    @Setter
    private Long id;

    @Getter
    private Long accountId;

    private FullName fullName;

    private EmailAddress email;

    @Getter
    private String photoUrl;

    /**
     * Creates a new UserProfile linked to an account.
     */
    public UserProfile(Long accountId, String fullName, String email, String photoUrl) {
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.fullName  = new FullName(fullName);
        this.email     = new EmailAddress(email);
        this.photoUrl  = photoUrl != null ? photoUrl : "";
    }

    /**
     * Reconstructs a UserProfile from persisted values (used by assembler).
     */
    public UserProfile(Long id, Long accountId, String fullName, String email, String photoUrl) {
        this.id        = id;
        this.accountId = accountId;
        this.fullName  = new FullName(fullName);
        this.email     = new EmailAddress(email);
        this.photoUrl  = photoUrl != null ? photoUrl : "";
    }

    /** @return the full name string */
    public String getFullName() { return fullName.value(); }

    /** @return the email address string */
    public String getEmail() { return email.address(); }

    /**
     * Updates profile fields from an {@link UpdateUserProfileCommand}.
     */
    public void update(UpdateUserProfileCommand command) {
        this.fullName = new FullName(Objects.requireNonNull(command.fullName(), "fullName must not be null"));
        this.photoUrl = command.photoUrl() != null ? command.photoUrl() : "";
    }
}
