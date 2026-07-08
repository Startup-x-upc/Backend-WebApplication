package org.example.backendwebapplication.iam.infrastructure.security;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistence entity for refresh tokens.
 * <p>NOT a domain aggregate — lives exclusively in the infrastructure .</p>
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    public RefreshTokenEntity() {}

    public RefreshTokenEntity(String token, UUID userId, Instant expiryDate) {
        this.token = token;
        this.userId = userId.toString();
        this.expiryDate = expiryDate;
        this.revoked = false;
    }

    public Long getId()                     { return id; }
    public String getToken()                { return token; }
    public String getUserId()               { return userId; }
    public Instant getExpiryDate()          { return expiryDate; }
    public boolean isRevoked()              { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
