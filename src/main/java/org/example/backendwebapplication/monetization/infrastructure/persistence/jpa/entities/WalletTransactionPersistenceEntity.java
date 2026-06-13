package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransactionPersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID walletId;

    @Column
    private UUID tripId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal resultingBalance;

    public WalletTransactionPersistenceEntity() {}

    public Long getId() { return id; }
    public UUID getWalletId() { return walletId; }
    public UUID getTripId() { return tripId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getResultingBalance() { return resultingBalance; }

    public void setId(Long id) { this.id = id; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }
    public void setTripId(UUID tripId) { this.tripId = tripId; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setResultingBalance(BigDecimal resultingBalance) { this.resultingBalance = resultingBalance; }
}