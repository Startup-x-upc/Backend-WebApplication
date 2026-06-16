package org.example.backendwebapplication.monetization.domain.model.commands;

import java.math.BigDecimal;

public record ConfigureFarePolicyCommand(
        BigDecimal baseFare,
        BigDecimal pricePerKm,
        BigDecimal minimumFare,
        BigDecimal commissionRate
) {}