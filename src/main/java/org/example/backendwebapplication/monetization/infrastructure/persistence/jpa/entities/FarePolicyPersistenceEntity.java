package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fare_policies")
public class FarePolicyPersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal baseFare;

    @Column(nullable = false)
    private BigDecimal pricePerKm;

    @Column(nullable = false)
    private BigDecimal minimumFare;

    public FarePolicyPersistenceEntity() {}

    public Long getId() { return id; }
    public BigDecimal getBaseFare() { return baseFare; }
    public BigDecimal getPricePerKm() { return pricePerKm; }
    public BigDecimal getMinimumFare() { return minimumFare; }

    public void setId(Long id) { this.id = id; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    public void setPricePerKm(BigDecimal pricePerKm) { this.pricePerKm = pricePerKm; }
    public void setMinimumFare(BigDecimal minimumFare) { this.minimumFare = minimumFare; }
}