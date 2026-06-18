package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletTransactionResponse(
        UUID id,
        UUID walletId,
        UUID tripId,
        String type,
        BigDecimal amount,
        BigDecimal resultingBalance,
        Instant timestamp
) {}