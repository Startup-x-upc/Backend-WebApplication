package org.example.backendwebapplication.monetization.application;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.model.commands.*;
import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.TransactionType;
import org.example.backendwebapplication.monetization.domain.repositories.FarePolicyRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class MonetizationCommandServiceImpl {

    private final FarePolicyRepository farePolicyRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public MonetizationCommandServiceImpl(FarePolicyRepository farePolicyRepository,
                                          WalletRepository walletRepository,
                                          WalletTransactionRepository walletTransactionRepository) {
        this.farePolicyRepository = farePolicyRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public FarePolicy handle(ConfigureFarePolicyCommand command) {
        FarePolicy policy = farePolicyRepository.getCurrent().orElse(new FarePolicy());
        policy.configure(command.baseFare(), command.pricePerKm(), command.minimumFare(), command.commissionRate());
        return farePolicyRepository.save(policy);
    }

    public Wallet handle(TopUpWalletCommand command) {
        Wallet wallet = walletRepository.findByDriverId(command.driverId())
                .orElse(new Wallet(command.driverId()));
        wallet.topUp(command.amount());
        Wallet saved = walletRepository.save(wallet);
        WalletTransaction tx = new WalletTransaction(
                saved.getWalletId(), null,
                TransactionType.TOP_UP, command.amount(), saved.getBalance());
        walletTransactionRepository.save(tx);
        return saved;
    }

    public WalletTransaction handle(RegisterTopUpFailureCommand command) {
        Wallet wallet = walletRepository.findByDriverId(command.driverId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        WalletTransaction tx = new WalletTransaction(
                wallet.getWalletId(), null,
                TransactionType.TOP_UP_FAILED, command.amount(), wallet.getBalance());
        return walletTransactionRepository.save(tx);
    }

    public WalletTransaction handle(ApplyRideCommissionCommand command) {
        Wallet wallet = walletRepository.findByDriverId(command.driverId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.applyCommission(command.rideFare());
        Wallet saved = walletRepository.save(wallet);
        WalletTransaction tx = new WalletTransaction(
                saved.getWalletId(), command.tripId(),
                TransactionType.COMMISSION, command.rideFare(), saved.getBalance());
        return walletTransactionRepository.save(tx);
    }

    public Wallet handle(BlockDriverWalletCommand command) {
        Wallet wallet = walletRepository.findByDriverId(command.driverId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.block();
        return walletRepository.save(wallet);
    }

    public Wallet handle(UnblockDriverWalletCommand command) {
        Wallet wallet = walletRepository.findByDriverId(command.driverId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.unblock();
        return walletRepository.save(wallet);
    }
}