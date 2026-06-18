package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.math.BigDecimal;

public record EstimatedFareResource(
        BigDecimal distanceKm
) {}