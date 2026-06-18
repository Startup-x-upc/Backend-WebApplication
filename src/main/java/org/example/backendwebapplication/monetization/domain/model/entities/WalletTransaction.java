package org.example.backendwebapplication.monetization.domain.model.entities;

import org.example.backendwebapplication.monetization.domain.model.valueobjects.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletTransaction {

    private UUID transactionId;
    private UUID walletId;
    private UUID tripId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal resultingBalance;
    private java.time.Instant timestamp;

    public WalletTransaction() {
        this.timestamp = java.time.Instant.now();
    }

    public WalletTransaction(UUID walletId, UUID tripId, TransactionType type,
                             BigDecimal amount, BigDecimal resultingBalance) {
        this.transactionId = UUID.randomUUID();
        this.walletId = walletId;
        this.tripId = tripId;
        this.type = type;
        this.amount = amount;
        this.resultingBalance = resultingBalance;
        this.timestamp = java.time.Instant.now();
    }

    public UUID getTransactionId() { return transactionId; }
    public UUID getWalletId() { return walletId; }
    public UUID getTripId() { return tripId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getResultingBalance() { return resultingBalance; }
    public java.time.Instant getTimestamp() { return timestamp; }

    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }
    public void setTripId(UUID tripId) { this.tripId = tripId; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setResultingBalance(BigDecimal resultingBalance) { this.resultingBalance = resultingBalance; }
    public void setTimestamp(java.time.Instant timestamp) { this.timestamp = timestamp; }
}