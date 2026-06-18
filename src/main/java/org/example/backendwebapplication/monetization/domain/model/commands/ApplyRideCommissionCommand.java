package org.example.backendwebapplication.monetization.domain.model.commands;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplyRideCommissionCommand(
        UUID walletId,
        UUID tripId,
        BigDecimal rideFare
) {}