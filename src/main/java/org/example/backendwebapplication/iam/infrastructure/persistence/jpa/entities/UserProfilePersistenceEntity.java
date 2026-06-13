package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities;

import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

/**
 * JPA persistence entity for the UserProfile aggregate.
 * Kept separate from the domain model to respect DDD layering.
 */
@Entity
@Table(name = "user_profiles")
public class UserProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String fullName;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false)
    private EmailAddress email;

    @Column
    private String photoUrl;

    public UserProfilePersistenceEntity() {}

    public Long getAccountId()                        { return accountId; }
    public void setAccountId(Long accountId)          { this.accountId = accountId; }
    public String getFullName()                       { return fullName; }
    public void setFullName(String fullName)          { this.fullName = fullName; }
    public EmailAddress getEmail()                    { return email; }
    public void setEmail(EmailAddress email)          { this.email = email; }
    public String getPhotoUrl()                       { return photoUrl; }
    public void setPhotoUrl(String photoUrl)          { this.photoUrl = photoUrl; }
}
