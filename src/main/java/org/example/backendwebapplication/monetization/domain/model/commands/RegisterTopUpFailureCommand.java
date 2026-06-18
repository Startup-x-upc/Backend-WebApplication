package org.example.backendwebapplication.monetization.domain.model.commands;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterTopUpFailureCommand(
        UUID walletId,
        BigDecimal amount,
        String reason
) {}