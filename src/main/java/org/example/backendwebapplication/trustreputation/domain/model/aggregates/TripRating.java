package org.example.backendwebapplication.trustreputation.domain.model.aggregates;

import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.RatingStatus;

import java.time.Instant;
import java.util.UUID;

public class TripRating extends AbstractDomainAggregateRoot<TripRating> {

    private UUID id;
    private UUID tripId;
    private UUID driverId;
    private UUID passengerId;
    private RatingStatus driverRatingStatus;
    private RatingStatus passengerRatingStatus;
    private Integer driverScore;
    private Integer passengerScore;
    private String passengerComment;
    private Instant rateableUntil;
    private Instant createdAt;

    public TripRating() {}

    public TripRating(UUID tripId, UUID driverId, UUID passengerId) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.driverRatingStatus = RatingStatus.PENDING;
        this.passengerRatingStatus = RatingStatus.PENDING;
        this.driverScore = null;
        this.passengerScore = null;
        this.passengerComment = null;
        this.createdAt = Instant.now();
        this.rateableUntil = this.createdAt.plus(java.time.Duration.ofHours(24));
    }

    public boolean isStillRateable() {
        return Instant.now().isBefore(rateableUntil);
    }

    public void checkExpiration() {
        if (!isStillRateable()) {
            if (this.driverRatingStatus == RatingStatus.PENDING) {
                this.driverRatingStatus = RatingStatus.EXPIRED;
            }
            if (this.passengerRatingStatus == RatingStatus.PENDING) {
                this.passengerRatingStatus = RatingStatus.EXPIRED;
            }
        }
    }

    public void rateDriver(int score) {
        checkExpiration();
        if (this.driverRatingStatus == RatingStatus.EXPIRED) {
            throw new IllegalStateException("RATING_WINDOW_EXPIRED: La ventana de 24h expiró");
        }
        if (this.driverRatingStatus != RatingStatus.PENDING) {
            throw new IllegalStateException("ALREADY_RATED: Ya calificaste a este conductor");
        }
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("INVALID_SCORE: Score debe ser 1-5");
        }
        this.driverScore = score;
        this.driverRatingStatus = RatingStatus.RATED;
    }

    public void ratePassenger(int score, String comment) {
        checkExpiration();
        if (this.passengerRatingStatus == RatingStatus.EXPIRED) {
            throw new IllegalStateException("RATING_WINDOW_EXPIRED: La ventana de 24h expiró");
        }
        if (this.passengerRatingStatus != RatingStatus.PENDING) {
            throw new IllegalStateException("ALREADY_RATED: Ya calificaste a este pasajero");
        }
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("INVALID_SCORE: Score debe ser 1-5");
        }
        if (score <= 2) {
            if (comment == null || comment.trim().isEmpty()) {
                throw new IllegalArgumentException("COMMENT_REQUIRED: Se requiere comentario si passengerScore <= 2");
            }
        }
        if (comment != null && comment.length() > 500) {
            throw new IllegalArgumentException("COMMENT_TOO_LONG: Comentario máximo de 500 caracteres");
        }
        this.passengerScore = score;
        this.passengerComment = comment;
        this.passengerRatingStatus = RatingStatus.RATED;
    }

    public void skipDriverRating() {
        checkExpiration();
        if (this.driverRatingStatus == RatingStatus.EXPIRED) {
            throw new IllegalStateException("RATING_WINDOW_EXPIRED: La ventana de 24h expiró");
        }
        if (this.driverRatingStatus != RatingStatus.PENDING) {
            throw new IllegalStateException("ALREADY_RATED: Ya calificaste o skippeaste a este conductor");
        }
        this.driverRatingStatus = RatingStatus.SKIPPED;
    }

    public void skipPassengerRating() {
        checkExpiration();
        if (this.passengerRatingStatus == RatingStatus.EXPIRED) {
            throw new IllegalStateException("RATING_WINDOW_EXPIRED: La ventana de 24h expiró");
        }
        if (this.passengerRatingStatus != RatingStatus.PENDING) {
            throw new IllegalStateException("ALREADY_RATED: Ya calificaste o skippeaste a este pasajero");
        }
        this.passengerRatingStatus = RatingStatus.SKIPPED;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTripId() { return tripId; }
    public void setTripId(UUID tripId) { this.tripId = tripId; }

    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }

    public UUID getPassengerId() { return passengerId; }
    public void setPassengerId(UUID passengerId) { this.passengerId = passengerId; }

    public RatingStatus getDriverRatingStatus() { return driverRatingStatus; }
    public void setDriverRatingStatus(RatingStatus driverRatingStatus) { this.driverRatingStatus = driverRatingStatus; }

    public RatingStatus getPassengerRatingStatus() { return passengerRatingStatus; }
    public void setPassengerRatingStatus(RatingStatus passengerRatingStatus) { this.passengerRatingStatus = passengerRatingStatus; }

    public Integer getDriverScore() { return driverScore; }
    public void setDriverScore(Integer driverScore) { this.driverScore = driverScore; }

    public Integer getPassengerScore() { return passengerScore; }
    public void setPassengerScore(Integer passengerScore) { this.passengerScore = passengerScore; }

    public String getPassengerComment() { return passengerComment; }
    public void setPassengerComment(String passengerComment) { this.passengerComment = passengerComment; }

    public Instant getRateableUntil() { return rateableUntil; }
    public void setRateableUntil(Instant rateableUntil) { this.rateableUntil = rateableUntil; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
