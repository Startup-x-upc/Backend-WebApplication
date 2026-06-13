package org.example.backendwebapplication.monetization.application;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.domain.model.queries.*;
import org.example.backendwebapplication.monetization.domain.repositories.FarePolicyRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MonetizationQueryServiceImpl {

    private final FarePolicyRepository farePolicyRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public MonetizationQueryServiceImpl(FarePolicyRepository farePolicyRepository,
                                        WalletRepository walletRepository,
                                        WalletTransactionRepository walletTransactionRepository) {
        this.farePolicyRepository = farePolicyRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public FarePolicy handle(GetCurrentFarePolicyQuery query) {
        return farePolicyRepository.getCurrent()
                .orElseThrow(() -> new RuntimeException("No fare policy configured"));
    }

    public BigDecimal handle(GetEstimatedFareQuery query) {
        FarePolicy policy = farePolicyRepository.getCurrent()
                .orElseThrow(() -> new RuntimeException("No fare policy configured"));
        return policy.calculate(query.distanceKm());
    }

    public Wallet handle(GetWalletByDriverIdQuery query) {
        return walletRepository.findByDriverId(query.driverId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public List<WalletTransaction> handle(GetWalletTransactionHistoryQuery query) {
        Wallet wallet = walletRepository.findByDriverId(query.driverId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return walletTransactionRepository.findByWalletId(wallet.getWalletId(), query.page(), query.size());
    }

    public boolean handle(CanDriverOperateQuery query) {
        return walletRepository.findByDriverId(query.driverId())
                .map(Wallet::hasPositiveBalance)
                .orElse(false);
    }
}