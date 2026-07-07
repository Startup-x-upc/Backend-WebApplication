package org.example.backendwebapplication.trustreputation.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.RatingStatus;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "trip_ratings")
public class TripRatingPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "rating_id", nullable = false, unique = true, length = 36)
    private String ratingId;

    @Column(name = "trip_id", nullable = false, unique = true, length = 36)
    private String tripId;

    @Column(name = "driver_id", nullable = false, length = 36)
    private String driverId;

    @Column(name = "passenger_id", nullable = false, length = 36)
    private String passengerId;

    @Column(name = "driver_rating_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RatingStatus driverRatingStatus;

    @Column(name = "passenger_rating_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RatingStatus passengerRatingStatus;

    @Column(name = "driver_score")
    private Integer driverScore;

    @Column(name = "passenger_score")
    private Integer passengerScore;

    @Column(name = "passenger_comment", length = 500)
    private String passengerComment;

    @Column(name = "rateable_until", nullable = false)
    private Instant rateableUntil;

    public TripRatingPersistenceEntity() {}
}
