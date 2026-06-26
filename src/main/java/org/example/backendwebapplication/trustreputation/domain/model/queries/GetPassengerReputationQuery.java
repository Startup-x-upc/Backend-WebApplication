package org.example.backendwebapplication.trustreputation.domain.model.queries;

import java.util.UUID;

public record GetPassengerReputationQuery(
        UUID passengerId
) {
    public GetPassengerReputationQuery {
        if (passengerId == null) {
            throw new IllegalArgumentException("Passenger ID cannot be null");
        }
    }
}
