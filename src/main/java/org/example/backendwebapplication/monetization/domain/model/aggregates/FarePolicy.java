package org.example.backendwebapplication.monetization.domain.model.aggregates;

import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.util.UUID;
import org.example.backendwebapplication.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

public class FarePolicy extends AbstractDomainAggregateRoot<FarePolicy> {

    /**
     * Represents a fare policy aggregate root in the monetization domain.
     * @summary Defines the pricing rules and commission structure for trip fares.
     * @see AbstractDomainAggregateRoot
     */
    private UUID farePolicyId;
    private BigDecimal baseFare;
    private BigDecimal pricePerKm;
    private BigDecimal minimumFare;
    private BigDecimal commissionRate;
    private java.time.Instant updatedAt;

    public FarePolicy() {}

    public FarePolicy(BigDecimal baseFare, BigDecimal pricePerKm, BigDecimal minimumFare, BigDecimal commissionRate) {
        this.farePolicyId = UUID.randomUUID();
        this.baseFare = baseFare;
        this.pricePerKm = pricePerKm;
        this.minimumFare = minimumFare;
        this.commissionRate = commissionRate;
    }

    public void configure(BigDecimal baseFare, BigDecimal pricePerKm, BigDecimal minimumFare, BigDecimal commissionRate) {
        this.baseFare = baseFare;
        this.pricePerKm = pricePerKm;
        this.minimumFare = minimumFare;
        this.commissionRate = commissionRate;
    }

    public BigDecimal calculate(BigDecimal distanceKm) {
        BigDecimal calculated = this.baseFare.add(this.pricePerKm.multiply(distanceKm));
        return calculated.compareTo(this.minimumFare) < 0 ? this.minimumFare : calculated;
    }

    public BigDecimal calculateCommission(BigDecimal fare) {
        return fare.multiply(this.commissionRate).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public UUID getFarePolicyId() { return farePolicyId; }
    public BigDecimal getBaseFare() { return baseFare; }
    public BigDecimal getPricePerKm() { return pricePerKm; }
    public BigDecimal getMinimumFare() { return minimumFare; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public java.time.Instant getUpdatedAt() { return updatedAt; }

    public void setFarePolicyId(UUID farePolicyId) { this.farePolicyId = farePolicyId; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    public void setPricePerKm(BigDecimal pricePerKm) { this.pricePerKm = pricePerKm; }
    public void setMinimumFare(BigDecimal minimumFare) { this.minimumFare = minimumFare; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    public void setUpdatedAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
}