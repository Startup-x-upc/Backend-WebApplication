package org.example.backendwebapplication.monetization.domain.repositories;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;

import java.util.Optional;

public interface FarePolicyRepository {
    FarePolicy save(FarePolicy farePolicy);
    Optional<FarePolicy> getCurrent();
}