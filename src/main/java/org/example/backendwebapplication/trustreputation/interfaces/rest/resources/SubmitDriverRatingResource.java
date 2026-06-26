package org.example.backendwebapplication.trustreputation.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SubmitDriverRatingResource(
        @Min(1) @Max(5)
        int score
) {}
