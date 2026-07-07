package org.example.backendwebapplication.monetization.application.internal.queryservices;

import org.example.backendwebapplication.monetization.application.queryservices.MonetizationQueryService;
import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.domain.model.queries.*;
import org.example.backendwebapplication.monetization.domain.repositories.FarePolicyRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletTransactionRepository;
import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MonetizationQueryServiceImpl implements MonetizationQueryService {

    private final FarePolicyRepository farePolicyRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final DriverContextFacade driverContextFacade;

    public MonetizationQueryServiceImpl(FarePolicyRepository farePolicyRepository,
                                        WalletRepository walletRepository,
                                        WalletTransactionRepository walletTransactionRepository,
                                        DriverContextFacade driverContextFacade) {
        this.farePolicyRepository = farePolicyRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.driverContextFacade = driverContextFacade;
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
        UUID resolvedId = resolveDriverId(query.driverId());
        return walletRepository.findByDriverId(resolvedId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public List<WalletTransaction> handle(GetWalletTransactionHistoryQuery query) {
        Wallet wallet = walletRepository.findByWalletId(query.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return walletTransactionRepository.findByWalletId(wallet.getWalletId(), query.page(), query.size());
    }

    public boolean handle(CanDriverOperateQuery query) {
        UUID resolvedId = resolveDriverId(query.driverId());
        var walletOpt = walletRepository.findByDriverId(resolvedId);
        if (walletOpt.isEmpty()) {
            return false;
        }
        var wallet = walletOpt.get();
        if (query.estimatedFare() != null) {
            FarePolicy policy = farePolicyRepository.getCurrent()
                    .orElseThrow(() -> new RuntimeException("No fare policy configured"));
            BigDecimal commission = policy.calculateCommission(query.estimatedFare());
            return wallet.getBalance().compareTo(commission) >= 0;
        }
        return wallet.hasPositiveBalance();
    }

    private UUID resolveDriverId(UUID driverId) {
        return driverContextFacade.getUserIdByDriverId(driverId)
                .orElse(driverId);
    }
}