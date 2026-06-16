package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.backendwebapplication.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "fare_policies")
public class FarePolicyPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private BigDecimal baseFare;

    @Column(nullable = false)
    private BigDecimal pricePerKm;

    @Column(nullable = false)
    private BigDecimal minimumFare;

    @Column(nullable = false)
    private BigDecimal commissionRate;

    public FarePolicyPersistenceEntity() {}

    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    public void setPricePerKm(BigDecimal pricePerKm) { this.pricePerKm = pricePerKm; }
    public void setMinimumFare(BigDecimal minimumFare) { this.minimumFare = minimumFare; }
}