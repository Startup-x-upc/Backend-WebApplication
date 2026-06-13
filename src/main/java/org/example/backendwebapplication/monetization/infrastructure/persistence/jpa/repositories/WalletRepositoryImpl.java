package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers.WalletPersistenceAssembler;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletPersistenceEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private final WalletJpaRepository jpaRepository;

    public WalletRepositoryImpl(WalletJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletPersistenceEntity entity = WalletPersistenceAssembler.toEntity(wallet);
        WalletPersistenceEntity saved = jpaRepository.save(entity);
        return WalletPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Wallet> findByDriverId(UUID driverId) {
        return jpaRepository.findByDriverId(driverId)
                .map(WalletPersistenceAssembler::toDomain);
    }
}