package org.example.backendwebapplication.monetization.application;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.model.commands.*;
import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.TransactionType;
import org.example.backendwebapplication.monetization.domain.repositories.FarePolicyRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.monetization.domain.repositories.WalletTransactionRepository;
import org.example.backendwebapplication.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MonetizationCommandServiceImpl {

    private final FarePolicyRepository farePolicyRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final IamContextFacade iamContextFacade;

    public MonetizationCommandServiceImpl(FarePolicyRepository farePolicyRepository,
                                          WalletRepository walletRepository,
                                          WalletTransactionRepository walletTransactionRepository,
                                          IamContextFacade iamContextFacade) {
        this.farePolicyRepository = farePolicyRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.iamContextFacade = iamContextFacade;
    }

    @Transactional
    public Wallet handle(CreateWalletCommand command) {
        if (!iamContextFacade.existsUserById(command.driverId())) {
            throw new IllegalArgumentException("Driver user does not exist in IAM");
        }
        if (walletRepository.findByDriverId(command.driverId()).isPresent()) {
            throw new IllegalStateException("Wallet already exists for this driver");
        }
        Wallet wallet = new Wallet(command.driverId());
        return walletRepository.save(wallet);
    }

    public FarePolicy handle(ConfigureFarePolicyCommand command) {
        FarePolicy policy = farePolicyRepository.getCurrent().orElse(new FarePolicy());
        policy.configure(command.baseFare(), command.pricePerKm(), command.minimumFare(), command.commissionRate());
        return farePolicyRepository.save(policy);
    }

    @Transactional
    public WalletRechargeResult handle(TopUpWalletCommand command) {
        Wallet wallet = walletRepository.findByWalletId(command.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        BigDecimal newBalance = wallet.getBalance().add(command.amount()).setScale(2, RoundingMode.HALF_UP);
        wallet.topUp(command.amount());
        Wallet saved = walletRepository.save(wallet);
        WalletTransaction tx = new WalletTransaction(
                saved.getWalletId(), null,
                TransactionType.TOP_UP, command.amount(), newBalance);
        WalletTransaction savedTx = walletTransactionRepository.save(tx);
        return new WalletRechargeResult(saved, savedTx);
    }

    @Transactional
    public WalletTransaction handle(RegisterTopUpFailureCommand command) {
        Wallet wallet = walletRepository.findByWalletId(command.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        WalletTransaction tx = new WalletTransaction(
                wallet.getWalletId(), null,
                TransactionType.TOP_UP_FAILED, command.amount(), wallet.getBalance());
        return walletTransactionRepository.save(tx);
    }

    @Transactional
    public WalletTransaction handle(ApplyRideCommissionCommand command) {
        Wallet wallet = walletRepository.findByWalletId(command.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        FarePolicy policy = farePolicyRepository.getCurrent()
                .orElseThrow(() -> new RuntimeException("No fare policy configured"));
        BigDecimal commission = policy.calculateCommission(command.rideFare());
        BigDecimal newBalance = wallet.getBalance().subtract(commission).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        wallet.setBalance(newBalance);
        Wallet saved = walletRepository.save(wallet);
        WalletTransaction tx = new WalletTransaction(
                saved.getWalletId(), command.tripId(),
                TransactionType.COMMISSION, commission.negate(), newBalance);
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