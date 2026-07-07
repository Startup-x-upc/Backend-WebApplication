package org.example.backendwebapplication.trustreputation.domain.model.commands;

import java.util.UUID;

public record SubmitDriverRatingCommand(
        UUID tripId,
        int score
) {
    public SubmitDriverRatingCommand {
        if (tripId == null) {
            throw new IllegalArgumentException("Trip ID cannot be null");
        }
    }
}
