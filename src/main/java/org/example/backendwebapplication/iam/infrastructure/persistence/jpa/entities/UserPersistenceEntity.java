package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities;

import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

/**
 * JPA persistence entity for the User aggregate.
 */
@Entity
@Table(name = "users")
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    public UserPersistenceEntity() {}

    public String getUserId()                    { return userId; }
    public void setUserId(String userId)         { this.userId = userId; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public String getPasswordHash()            { return passwordHash; }
    public void setPasswordHash(String hash)   { this.passwordHash = hash; }
    public UserRole getRole()                  { return role; }
    public void setRole(UserRole role)         { this.role = role; }
}
