package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.WalletStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class WalletPersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID driverId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    public WalletPersistenceEntity() {}

    public Long getId() { return id; }
    public UUID getDriverId() { return driverId; }
    public BigDecimal getBalance() { return balance; }
    public WalletStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setStatus(WalletStatus status) { this.status = status; }
}