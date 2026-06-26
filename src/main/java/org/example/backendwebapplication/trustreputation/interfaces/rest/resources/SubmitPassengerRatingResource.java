package org.example.backendwebapplication.trustreputation.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record SubmitPassengerRatingResource(
        @Min(1) @Max(5)
        int score,

        @Size(max = 500)
        String comment
) {}
