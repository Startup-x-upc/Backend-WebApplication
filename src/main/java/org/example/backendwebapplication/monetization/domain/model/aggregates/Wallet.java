package org.example.backendwebapplication.monetization.domain.model.aggregates;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.WalletStatus;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.util.UUID;

public class Wallet extends AbstractDomainAggregateRoot<Wallet> {

    private UUID walletId;
    private UUID driverId;
    private BigDecimal balance;
    private WalletStatus status;

    public Wallet() {}

    public Wallet(UUID driverId) {
        this.walletId = UUID.randomUUID();
        this.driverId = driverId;
        this.balance = BigDecimal.ZERO;
        this.status = WalletStatus.ACTIVE;
    }

    public void topUp(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void applyCommission(BigDecimal amount) {
        this.balance = this.balance.subtract(amount).max(BigDecimal.ZERO).setScale(2, java.math.RoundingMode.HALF_UP);
        if (this.balance.compareTo(BigDecimal.ZERO) == 0) {
            registerDomainEvent(new org.example.backendwebapplication.monetization.domain.model.events.WalletEmptyEvent(this.walletId, this.driverId));
        }
    }

    public void block() {
        this.status = WalletStatus.BLOCKED;
    }

    public void unblock() {
        this.status = WalletStatus.ACTIVE;
    }

    public boolean hasPositiveBalance() {
        return this.balance.compareTo(BigDecimal.ZERO) > 0;
    }

    public UUID getWalletId() { return walletId; }
    public UUID getDriverId() { return driverId; }
    public BigDecimal getBalance() { return balance; }
    public WalletStatus getStatus() { return status; }

    public void setWalletId(UUID walletId) { this.walletId = walletId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setStatus(WalletStatus status) { this.status = status; }
}