package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletTransactionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionJpaRepository extends JpaRepository<WalletTransactionPersistenceEntity, Long> {
    List<WalletTransactionPersistenceEntity> findByWalletId(UUID walletId);
}