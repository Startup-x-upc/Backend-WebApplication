package org.example.backendwebapplication.monetization.domain.model.queries;

import java.util.UUID;

public record GetWalletTransactionHistoryQuery(
        UUID driverId,
        int page,
        int size
) {}