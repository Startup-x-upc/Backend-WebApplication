package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities;

import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

/**
 * JPA persistence entity for the Account aggregate.
 * Kept separate from the domain model to respect DDD layering.
 */
@Entity
@Table(name = "accounts")
public class AccountPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false, unique = true)
    private EmailAddress email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    public AccountPersistenceEntity() {}

    public EmailAddress getEmail()                    { return email; }
    public void setEmail(EmailAddress email)          { this.email = email; }
    public String getPasswordHash()                   { return passwordHash; }
    public void setPasswordHash(String passwordHash)  { this.passwordHash = passwordHash; }
    public UserRole getRole()                         { return role; }
    public void setRole(UserRole role)                { this.role = role; }
}
