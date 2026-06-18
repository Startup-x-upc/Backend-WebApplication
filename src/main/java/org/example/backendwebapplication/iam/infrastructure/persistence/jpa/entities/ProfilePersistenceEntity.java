package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities;

import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

/**
 * JPA persistence entity for the Profile entity.
 */
@Entity
@Table(name = "profiles")
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "profile_id", nullable = false, unique = true, length = 36)
    private String profileId;

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 500)
    private String photoUrl;

    public ProfilePersistenceEntity() {}

    public String getProfileId()                   { return profileId; }
    public void setProfileId(String profileId)     { this.profileId = profileId; }
    public String getUserId()                      { return userId; }
    public void setUserId(String userId)           { this.userId = userId; }
    public String getFullName()                  { return fullName; }
    public void setFullName(String fullName)     { this.fullName = fullName; }
    public String getPhotoUrl()                  { return photoUrl; }
    public void setPhotoUrl(String photoUrl)     { this.photoUrl = photoUrl; }
}
