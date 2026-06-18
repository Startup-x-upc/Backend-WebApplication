package org.example.backendwebapplication.monetization.domain.model.queries;

import java.util.UUID;

public record GetWalletTransactionHistoryQuery(
        UUID walletId,
        int page,
        int size
) {}