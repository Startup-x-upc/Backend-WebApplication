package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletTransactionResponse(
        UUID transactionId,
        UUID walletId,
        UUID tripId,
        String type,
        BigDecimal amount,
        BigDecimal resultingBalance
) {}