package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities;

import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * JPA persistence entity for the Profile entity.
 * <p>Kept in the infrastructure layer to keep JPA concerns out of the
 * domain model. The business identity ({@code profileId}) is a UUID column
 * separate from the auto-generated {@code id} primary key.</p>
 */
@Entity
@Table(name = "profiles")
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, unique = true, length = 36)
    private UUID profileId;

    @Column(nullable = false, unique = true, length = 36)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 500)
    private String photoUrl;

    public ProfilePersistenceEntity() {}

    // ── Getters / Setters ─────────────────────────────────────────────

    public UUID getProfileId()                   { return profileId; }
    public void setProfileId(UUID profileId)     { this.profileId = profileId; }
    public UUID getUserId()                      { return userId; }
    public void setUserId(UUID userId)           { this.userId = userId; }
    public String getFullName()                  { return fullName; }
    public void setFullName(String fullName)     { this.fullName = fullName; }
    public String getPhotoUrl()                  { return photoUrl; }
    public void setPhotoUrl(String photoUrl)     { this.photoUrl = photoUrl; }
}
