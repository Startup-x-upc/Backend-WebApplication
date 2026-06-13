package org.example.backendwebapplication.monetization.domain.model.aggregates;

import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.util.UUID;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

public class FarePolicy extends AbstractDomainAggregateRoot<FarePolicy> {

    private UUID farePolicyId;
    private BigDecimal baseFare;
    private BigDecimal pricePerKm;
    private BigDecimal minimumFare;

    public FarePolicy() {}

    public FarePolicy(BigDecimal baseFare, BigDecimal pricePerKm, BigDecimal minimumFare) {
        this.farePolicyId = UUID.randomUUID();
        this.baseFare = baseFare;
        this.pricePerKm = pricePerKm;
        this.minimumFare = minimumFare;
    }

    public void configure(BigDecimal baseFare, BigDecimal pricePerKm, BigDecimal minimumFare) {
        this.baseFare = baseFare;
        this.pricePerKm = pricePerKm;
        this.minimumFare = minimumFare;
    }

    public BigDecimal calculate(BigDecimal distanceKm) {
        BigDecimal calculated = this.baseFare.add(this.pricePerKm.multiply(distanceKm));
        return calculated.compareTo(this.minimumFare) < 0 ? this.minimumFare : calculated;
    }

    public UUID getFarePolicyId() { return farePolicyId; }
    public BigDecimal getBaseFare() { return baseFare; }
    public BigDecimal getPricePerKm() { return pricePerKm; }
    public BigDecimal getMinimumFare() { return minimumFare; }

    public void setFarePolicyId(UUID farePolicyId) { this.farePolicyId = farePolicyId; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    public void setPricePerKm(BigDecimal pricePerKm) { this.pricePerKm = pricePerKm; }
    public void setMinimumFare(BigDecimal minimumFare) { this.minimumFare = minimumFare; }
}