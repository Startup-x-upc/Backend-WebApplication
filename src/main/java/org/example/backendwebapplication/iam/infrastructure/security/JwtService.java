package org.example.backendwebapplication.iam.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Stateless service for JWT token operations.
 * <p>Access tokens are signed JWTs with 15-minute TTL.
 * Refresh tokens are opaque random strings stored in the database with 7-day TTL.</p>
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JwtService(@Value("${app.jwt.secret:ChapaTuRuta_UltraSecretKey_Min256Bits_ChangeInProduction!}")
                      String secret,
                      RefreshTokenRepository refreshTokenRepository,
                      UserRepository userRepository) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // ── Access Token ────────────────────────────────────────────────────

    /**
     * Generates a signed JWT access token for the given user.
     */
    public String generateAccessToken(User user) {
        var now = Instant.now();
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(15, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates and parses an access token. Returns the claims if valid.
     *
     * @throws JwtException if the token is invalid, expired, or malformed
     */
    public Jws<Claims> parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
    }

    /**
     * Convenience: extracts the userId claim from a valid access token.
     */
    public UUID extractUserId(String token) {
        var claims = parseAccessToken(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }

    // ── Refresh Token ───────────────────────────────────────────────────

    /**
     * Generates an opaque refresh token, persists it, and returns the token string.
     */
    @Transactional
    public String generateRefreshToken(User user) {
        var token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        var entity = new RefreshTokenEntity(
                token,
                user.getUserId(),
                Instant.now().plus(7, ChronoUnit.DAYS));
        refreshTokenRepository.save(entity);
        return token;
    }

    /**
     * Refreshes an access token using a valid refresh token.
     * <p>Rotates the refresh token: the old one is revoked, and a new pair
     * (access + refresh) is issued.</p>
     *
     * @param refreshToken the current refresh token
     * @param user         the authenticated user (loaded from the revoked token's userId)
     * @return a new {@link TokenPair}
     * @throws JwtException if the refresh token is invalid or expired
     */
    @Transactional
    public TokenPair refreshAccessToken(String refreshToken, User user) {
        var stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new JwtException("Refresh token not found"));

        if (!stored.isValid()) {
            throw new JwtException("Refresh token is revoked or expired");
        }

        // Rotate: revoke the old token
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        // Issue new pair
        var newAccessToken = generateAccessToken(user);
        var newRefreshToken = generateRefreshToken(user);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    /**
     * Full refresh flow: validates the refresh token, loads the associated
     * User, rotates the token, and returns a new pair.
     *
     * @param refreshToken the current refresh token string
     * @return a new {@link TokenPair}
     * @throws JwtException if the refresh token is invalid, expired, or the user is gone
     */
    @Transactional
    public TokenPair refreshAccessToken(String refreshToken) {
        var stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new JwtException("Refresh token not found"));

        if (!stored.isValid()) {
            throw new JwtException("Refresh token is revoked or expired");
        }

        var user = userRepository.findById(UUID.fromString(stored.getUserId()))
                .orElseThrow(() -> new JwtException("User associated with refresh token no longer exists"));

        // Rotate: revoke the old token
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        // Issue new pair
        var newAccessToken = generateAccessToken(user);
        var newRefreshToken = generateRefreshToken(user);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    /**
     * Invalidates all refresh tokens for a given user (e.g., on logout).
     */
    @Transactional
    public void revokeAllRefreshTokens(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId.toString());
    }

    // ── Token Pair ──────────────────────────────────────────────────────

    /**
     * Holds an access/refresh token pair returned after authentication.
     */
    public record TokenPair(String accessToken, String refreshToken) {}
}
