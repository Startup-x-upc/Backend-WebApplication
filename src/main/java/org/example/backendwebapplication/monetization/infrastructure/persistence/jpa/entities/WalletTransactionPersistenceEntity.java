package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "wallet_transactions")
public class WalletTransactionPersistenceEntity extends AuditableAbstractPersistenceEntity {

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

    public void setWalletId(UUID walletId) { this.walletId = walletId; }
    public void setTripId(UUID tripId) { this.tripId = tripId; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setResultingBalance(BigDecimal resultingBalance) { this.resultingBalance = resultingBalance; }
}