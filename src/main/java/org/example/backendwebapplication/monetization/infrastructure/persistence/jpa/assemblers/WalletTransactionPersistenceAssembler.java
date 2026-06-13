package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletTransactionPersistenceEntity;

public class WalletTransactionPersistenceAssembler {

    public static WalletTransactionPersistenceEntity toEntity(WalletTransaction domain) {
        WalletTransactionPersistenceEntity entity = new WalletTransactionPersistenceEntity();
        entity.setWalletId(domain.getWalletId());
        entity.setTripId(domain.getTripId());
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount());
        entity.setResultingBalance(domain.getResultingBalance());
        return entity;
    }

    public static WalletTransaction toDomain(WalletTransactionPersistenceEntity entity) {
        WalletTransaction domain = new WalletTransaction();
        domain.setWalletId(entity.getWalletId());
        domain.setTripId(entity.getTripId());
        domain.setType(entity.getType());
        domain.setAmount(entity.getAmount());
        domain.setResultingBalance(entity.getResultingBalance());
        return domain;
    }
}