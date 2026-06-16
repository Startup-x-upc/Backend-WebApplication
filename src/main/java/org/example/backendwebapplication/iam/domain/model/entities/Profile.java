package org.example.backendwebapplication.iam.domain.model.entities;

import org.example.backendwebapplication.iam.domain.model.valueobjects.FullName;

import java.util.UUID;

/**
 * Profile entity (subordinate, not an aggregate root).
 * <p>Holds the display information of a registered user: full name and
 * optional photo URL. Each profile is associated 1:1 with a {@code User}
 * aggregate via {@code userId}.</p>
 *
 * <p>Email and role are intentionally NOT stored here — they belong to
 * {@link org.example.backendwebapplication.iam.domain.model.aggregates.User}.
 * The read model returned by {@code GET /users/me/profile} composes both.</p>
 */
public class Profile {

    private UUID profileId;
    private UUID userId;
    private FullName fullName;
    private String photoUrl;

    /** JPA-friendly constructor. */
    Profile() {
    }

    /**
     * Creates a new Profile linked to a User.
     */
    public Profile(UUID userId, FullName fullName, String photoUrl) {
        this.profileId = UUID.randomUUID();
        this.userId = userId;
        this.fullName = fullName;
        this.photoUrl = (photoUrl != null) ? photoUrl : "";
    }

    /**
     * Full reconstruction constructor (used by persistence assembler).
     */
    public Profile(UUID profileId, UUID userId, FullName fullName, String photoUrl) {
        this.profileId = profileId;
        this.userId = userId;
        this.fullName = fullName;
        this.photoUrl = (photoUrl != null) ? photoUrl : "";
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public UUID getProfileId() {
        return profileId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName.value();
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    // ── Domain behaviour ─────────────────────────────────────────────────

    /**
     * Updates mutable profile fields.
     */
    public void update(FullName newFullName, String newPhotoUrl) {
        this.fullName = newFullName;
        this.photoUrl = (newPhotoUrl != null) ? newPhotoUrl : "";
    }

    // ── Object contracts ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile other)) return false;
        return profileId.equals(other.profileId);
    }

    @Override
    public int hashCode() {
        return profileId.hashCode();
    }

    @Override
    public String toString() {
        return "Profile{profileId=%s, userId=%s, fullName=%s}"
                .formatted(profileId, userId, fullName.value());
    }
}
