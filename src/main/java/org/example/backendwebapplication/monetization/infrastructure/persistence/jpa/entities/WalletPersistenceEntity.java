package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.WalletStatus;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "wallets")
public class WalletPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "driver_id", nullable = false, length = 36)
    private String driverId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    @Column(name = "wallet_id", nullable = false, unique = true, length = 36)
    private String walletId;

    public WalletPersistenceEntity() {}

    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setStatus(WalletStatus status) { this.status = status; }
    public void setWalletId(String walletId) { this.walletId = walletId; }
}
