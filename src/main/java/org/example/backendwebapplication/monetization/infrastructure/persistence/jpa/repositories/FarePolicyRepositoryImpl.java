package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.repositories.FarePolicyRepository;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers.FarePolicyPersistenceAssembler;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.FarePolicyPersistenceEntity;
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
        FarePolicyPersistenceEntity entity = FarePolicyPersistenceAssembler.toEntity(farePolicy);
        FarePolicyPersistenceEntity saved = jpaRepository.save(entity);
        return FarePolicyPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<FarePolicy> getCurrent() {
        return jpaRepository.findAll().stream().findFirst()
                .map(FarePolicyPersistenceAssembler::toDomain);
    }
}