package org.example.backendwebapplication.trustreputation.domain.model.queries;

import java.util.UUID;

public record GetTripRatingByTripIdQuery(
        UUID tripId
) {
    public GetTripRatingByTripIdQuery {
        if (tripId == null) {
            throw new IllegalArgumentException("Trip ID cannot be null");
        }
    }
}
