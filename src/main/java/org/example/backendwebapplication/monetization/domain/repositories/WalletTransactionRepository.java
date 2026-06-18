package org.example.backendwebapplication.monetization.domain.repositories;

import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionRepository {
    WalletTransaction save(WalletTransaction transaction);
    List<WalletTransaction> findByWalletId(UUID walletId, int page, int size);
}