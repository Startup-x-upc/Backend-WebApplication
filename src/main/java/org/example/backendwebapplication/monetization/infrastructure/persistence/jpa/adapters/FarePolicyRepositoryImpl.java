package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.repositories.FarePolicyRepository;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers.FarePolicyPersistenceAssembler;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.FarePolicyPersistenceEntity;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories.FarePolicyJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FarePolicyRepositoryImpl implements FarePolicyRepository {

    private final FarePolicyJpaRepository jpaRepository;

    public FarePolicyRepositoryImpl(FarePolicyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FarePolicy save(FarePolicy farePolicy) {
        var existing = jpaRepository.findByFarePolicyId(farePolicy.getFarePolicyId().toString())
                .orElse(null);
        FarePolicyPersistenceEntity entity = FarePolicyPersistenceAssembler.toEntity(farePolicy);
        if (existing != null) {
            entity.setId(existing.getId());
        }
        FarePolicyPersistenceEntity saved = jpaRepository.save(entity);
        return FarePolicyPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<FarePolicy> getCurrent() {
        return jpaRepository.findAll().stream().findFirst()
                .map(FarePolicyPersistenceAssembler::toDomain);
    }
}