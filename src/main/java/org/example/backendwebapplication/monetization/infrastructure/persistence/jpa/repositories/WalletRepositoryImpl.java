package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers.WalletPersistenceAssembler;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletPersistenceEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private final WalletJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public WalletRepositoryImpl(WalletJpaRepository jpaRepository,
                                ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletPersistenceEntity entity = WalletPersistenceAssembler.toEntity(wallet);
        WalletPersistenceEntity saved = jpaRepository.save(entity);

        // Publish domain events registered on the aggregate
        wallet.domainEvents().forEach(eventPublisher::publishEvent);
        wallet.clearDomainEvents();

        return WalletPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Wallet> findByDriverId(UUID driverId) {
        return jpaRepository.findByDriverId(driverId)
                .map(WalletPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Wallet> findByWalletId(UUID walletId) {
        return jpaRepository.findByWalletId(walletId)
                .map(WalletPersistenceAssembler::toDomain);
    }
}
