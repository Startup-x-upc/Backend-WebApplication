package org.example.backendwebapplication.trustreputation.domain.model.queries;

import java.util.UUID;

public record GetDriverReputationQuery(
        UUID driverId
) {
    public GetDriverReputationQuery {
        if (driverId == null) {
            throw new IllegalArgumentException("Driver ID cannot be null");
        }
    }
}
