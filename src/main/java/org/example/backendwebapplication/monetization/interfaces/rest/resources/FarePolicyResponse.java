package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FarePolicyResponse(
        UUID id,
        BigDecimal baseFare,
        BigDecimal pricePerKm,
        BigDecimal minimumFare,
        BigDecimal commissionRate,
        Instant updatedAt
) {}