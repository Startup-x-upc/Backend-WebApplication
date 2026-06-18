package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers.WalletPersistenceAssembler;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletPersistenceEntity;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories.WalletJpaRepository;
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
        var existing = jpaRepository.findByWalletId(wallet.getWalletId().toString())
                .orElse(null);
        WalletPersistenceEntity entity = WalletPersistenceAssembler.toEntity(wallet);
        if (existing != null) {
            entity.setId(existing.getId());
        }
        WalletPersistenceEntity saved = jpaRepository.save(entity);

        wallet.domainEvents().forEach(eventPublisher::publishEvent);
        wallet.clearDomainEvents();

        return WalletPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Wallet> findByDriverId(UUID driverId) {
        return jpaRepository.findByDriverId(driverId.toString())
                .map(WalletPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Wallet> findByWalletId(UUID walletId) {
        return jpaRepository.findByWalletId(walletId.toString())
                .map(WalletPersistenceAssembler::toDomain);
    }
}
