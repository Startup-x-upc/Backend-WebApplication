package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.FarePolicyPersistenceEntity;

public class FarePolicyPersistenceAssembler {

    public static FarePolicyPersistenceEntity toEntity(FarePolicy domain) {
        FarePolicyPersistenceEntity entity = new FarePolicyPersistenceEntity();
        entity.setBaseFare(domain.getBaseFare());
        entity.setPricePerKm(domain.getPricePerKm());
        entity.setMinimumFare(domain.getMinimumFare());
        entity.setCommissionRate(domain.getCommissionRate());
        return entity;
    }

    public static FarePolicy toDomain(FarePolicyPersistenceEntity entity) {
        FarePolicy domain = new FarePolicy();
        domain.setBaseFare(entity.getBaseFare());
        domain.setPricePerKm(entity.getPricePerKm());
        domain.setMinimumFare(entity.getMinimumFare());
        domain.setCommissionRate(entity.getCommissionRate());
        return domain;
    }
}