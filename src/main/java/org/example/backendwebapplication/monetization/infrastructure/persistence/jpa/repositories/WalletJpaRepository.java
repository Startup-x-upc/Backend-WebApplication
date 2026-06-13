package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletJpaRepository extends JpaRepository<WalletPersistenceEntity, Long> {
    Optional<WalletPersistenceEntity> findByDriverId(UUID driverId);
}