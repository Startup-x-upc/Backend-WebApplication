package org.example.backendwebapplication.trustreputation.domain.model.commands;

import java.util.UUID;

public record SubmitPassengerRatingCommand(
        UUID tripId,
        int score,
        String comment
) {
    public SubmitPassengerRatingCommand {
        if (tripId == null) {
            throw new IllegalArgumentException("Trip ID cannot be null");
        }
    }
}
