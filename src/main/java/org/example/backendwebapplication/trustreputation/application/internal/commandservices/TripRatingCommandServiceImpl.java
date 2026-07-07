package org.example.backendwebapplication.trustreputation.application.internal.commandservices;

import org.example.backendwebapplication.trustreputation.application.commandservices.TripRatingCommandService;
import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;
import org.example.backendwebapplication.trustreputation.domain.model.commands.SubmitDriverRatingCommand;
import org.example.backendwebapplication.trustreputation.domain.model.commands.SubmitPassengerRatingCommand;
import org.example.backendwebapplication.trustreputation.domain.repositories.TripRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TripRatingCommandServiceImpl implements TripRatingCommandService {

    private final TripRatingRepository repository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    public TripRatingCommandServiceImpl(TripRatingRepository repository,
                                       org.springframework.context.ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    private void publishDriverReputationUpdated(UUID driverId) {
        Double avg = repository.findAverageDriverScore(driverId);
        Long count = repository.countDriverRatings(driverId);
        double averageScore = avg != null ? avg : 0.0;
        long totalRatings = count != null ? count : 0L;
        eventPublisher.publishEvent(new org.example.backendwebapplication.trustreputation.domain.model.events.DriverReputationUpdatedEvent(driverId, averageScore, totalRatings));
    }

    private void publishPassengerReputationUpdated(UUID passengerId) {
        Double avg = repository.findAveragePassengerScore(passengerId);
        Long count = repository.countPassengerRatings(passengerId);
        double averageScore = avg != null ? avg : 0.0;
        long totalRatings = count != null ? count : 0L;
        eventPublisher.publishEvent(new org.example.backendwebapplication.trustreputation.domain.model.events.PassengerReputationUpdatedEvent(passengerId, averageScore, totalRatings));
    }

    @Override
    @Transactional
    public TripRating handle(SubmitDriverRatingCommand command) {
        TripRating tripRating = repository.findByTripId(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));
        tripRating.rateDriver(command.score());
        TripRating saved = repository.save(tripRating);
        publishDriverReputationUpdated(saved.getDriverId());
        return saved;
    }

    @Override
    @Transactional
    public TripRating handle(SubmitPassengerRatingCommand command) {
        TripRating tripRating = repository.findByTripId(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));
        tripRating.ratePassenger(command.score(), command.comment());
        TripRating saved = repository.save(tripRating);
        publishPassengerReputationUpdated(saved.getPassengerId());
        return saved;
    }

    @Override
    @Transactional
    public TripRating handleSkipDriverRating(UUID tripId) {
        TripRating tripRating = repository.findByTripId(tripId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));
        tripRating.skipDriverRating();
        TripRating saved = repository.save(tripRating);
        publishDriverReputationUpdated(saved.getDriverId());
        return saved;
    }

    @Override
    @Transactional
    public TripRating handleSkipPassengerRating(UUID tripId) {
        TripRating tripRating = repository.findByTripId(tripId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));
        tripRating.skipPassengerRating();
        TripRating saved = repository.save(tripRating);
        publishPassengerReputationUpdated(saved.getPassengerId());
        return saved;
    }

    @Override
    @Transactional
    public TripRating handleCreateTripRating(UUID tripId, UUID driverId, UUID passengerId) {
        return repository.findByTripId(tripId)
                .orElseGet(() -> {
                    TripRating rating = new TripRating(tripId, driverId, passengerId);
                    return repository.save(rating);
                });
    }
}
