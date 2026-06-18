package org.example.backendwebapplication.monetization.domain.model.queries;

import java.math.BigDecimal;

public record GetEstimatedFareQuery(
        BigDecimal distanceKm
) {}