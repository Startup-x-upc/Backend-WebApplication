package org.example.backendwebapplication.trustreputation.application.internal.queryservices;

import org.example.backendwebapplication.trustreputation.application.queryservices.TripRatingQueryService;
import org.example.backendwebapplication.trustreputation.domain.model.aggregates.TripRating;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetDriverReputationQuery;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetPassengerReputationQuery;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetTripRatingByTripIdQuery;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.DriverReputation;
import org.example.backendwebapplication.trustreputation.domain.model.valueobjects.PassengerReputation;
import org.example.backendwebapplication.trustreputation.domain.repositories.TripRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TripRatingQueryServiceImpl implements TripRatingQueryService {

    private final TripRatingRepository repository;

    public TripRatingQueryServiceImpl(TripRatingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TripRating> handle(GetTripRatingByTripIdQuery query) {
        return repository.findByTripId(query.tripId());
    }

    @Override
    @Transactional(readOnly = true)
    public DriverReputation handle(GetDriverReputationQuery query) {
        Double avg = repository.findAverageDriverScore(query.driverId());
        Long count = repository.countDriverRatings(query.driverId());
        double averageScore = avg != null ? avg : 0.0;
        long totalRatings = count != null ? count : 0L;
        return new DriverReputation(query.driverId(), averageScore, totalRatings);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerReputation handle(GetPassengerReputationQuery query) {
        Double avg = repository.findAveragePassengerScore(query.passengerId());
        Long count = repository.countPassengerRatings(query.passengerId());
        double averageScore = avg != null ? avg : 0.0;
        long totalRatings = count != null ? count : 0L;
        return new PassengerReputation(query.passengerId(), averageScore, totalRatings);
    }
}
