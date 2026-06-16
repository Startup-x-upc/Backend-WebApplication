package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletPersistenceEntity;

public class WalletPersistenceAssembler {

    public static WalletPersistenceEntity toEntity(Wallet domain) {
        WalletPersistenceEntity entity = new WalletPersistenceEntity();
        entity.setDriverId(domain.getDriverId());
        entity.setBalance(domain.getBalance());
        entity.setStatus(domain.getStatus());
        entity.setWalletId(domain.getWalletId());
        return entity;
    }

    public static Wallet toDomain(WalletPersistenceEntity entity) {
        Wallet domain = new Wallet();
        domain.setDriverId(entity.getDriverId());
        domain.setBalance(entity.getBalance());
        domain.setStatus(entity.getStatus());
        domain.setWalletId(entity.getWalletId());
        return domain;
    }
}