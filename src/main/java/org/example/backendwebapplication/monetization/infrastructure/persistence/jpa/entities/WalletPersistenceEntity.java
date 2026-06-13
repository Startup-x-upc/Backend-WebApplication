package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.WalletStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "wallets")
public class WalletPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private UUID driverId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    public WalletPersistenceEntity() {}

    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setStatus(WalletStatus status) { this.status = status; }
}