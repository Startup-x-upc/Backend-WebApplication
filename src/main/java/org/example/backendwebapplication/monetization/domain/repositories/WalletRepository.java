package org.example.backendwebapplication.monetization.domain.repositories;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findByDriverId(UUID driverId);
    Optional<Wallet> findByWalletId(UUID walletId);
}