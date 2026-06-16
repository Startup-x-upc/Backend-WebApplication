package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.TransactionType;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "wallet_transactions")
public class WalletTransactionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "wallet_id", nullable = false, length = 36)
    private String walletId;

    @Column(name = "trip_id", length = 36)
    private String tripId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal resultingBalance;

    public WalletTransactionPersistenceEntity() {}

    public void setWalletId(String walletId) { this.walletId = walletId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    public void setType(TransactionType type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setResultingBalance(BigDecimal resultingBalance) { this.resultingBalance = resultingBalance; }
}
