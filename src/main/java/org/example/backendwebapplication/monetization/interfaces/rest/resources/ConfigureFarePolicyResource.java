package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.math.BigDecimal;

public record ConfigureFarePolicyResource(
        BigDecimal baseFare,
        BigDecimal pricePerKm,
        BigDecimal minimumFare
) {}