package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.domain.repositories.WalletTransactionRepository;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers.WalletTransactionPersistenceAssembler;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletTransactionPersistenceEntity;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories.WalletTransactionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class WalletTransactionRepositoryImpl implements WalletTransactionRepository {

    private final WalletTransactionJpaRepository jpaRepository;

    public WalletTransactionRepositoryImpl(WalletTransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WalletTransaction save(WalletTransaction transaction) {
        WalletTransactionPersistenceEntity entity = WalletTransactionPersistenceAssembler.toEntity(transaction);
        WalletTransactionPersistenceEntity saved = jpaRepository.save(entity);
        return WalletTransactionPersistenceAssembler.toDomain(saved);
    }

    @Override
    public List<WalletTransaction> findByWalletId(UUID walletId, int page, int size) {
        return jpaRepository.findByWalletId(walletId.toString()).stream()
                .skip((long) page * size)
                .limit(size)
                .map(WalletTransactionPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }
}
