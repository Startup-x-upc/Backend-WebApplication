package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletPersistenceEntity;

import java.util.UUID;

public class WalletPersistenceAssembler {

    public static WalletPersistenceEntity toEntity(Wallet domain) {
        WalletPersistenceEntity entity = new WalletPersistenceEntity();
        entity.setDriverId(domain.getDriverId().toString());
        entity.setBalance(domain.getBalance());
        entity.setStatus(domain.getStatus());
        entity.setWalletId(domain.getWalletId().toString());
        return entity;
    }

    public static Wallet toDomain(WalletPersistenceEntity entity) {
        Wallet domain = new Wallet();
        domain.setDriverId(UUID.fromString(entity.getDriverId()));
        domain.setBalance(entity.getBalance());
        domain.setStatus(entity.getStatus());
        domain.setWalletId(UUID.fromString(entity.getWalletId()));
        return domain;
    }
}
