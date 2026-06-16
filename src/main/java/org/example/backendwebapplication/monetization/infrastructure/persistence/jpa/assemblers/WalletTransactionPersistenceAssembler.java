package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.WalletTransactionPersistenceEntity;

import java.util.UUID;

public class WalletTransactionPersistenceAssembler {

    public static WalletTransactionPersistenceEntity toEntity(WalletTransaction domain) {
        WalletTransactionPersistenceEntity entity = new WalletTransactionPersistenceEntity();
        entity.setWalletId(domain.getWalletId().toString());
        entity.setTripId(domain.getTripId() != null ? domain.getTripId().toString() : null);
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount());
        entity.setResultingBalance(domain.getResultingBalance());
        return entity;
    }

    public static WalletTransaction toDomain(WalletTransactionPersistenceEntity entity) {
        WalletTransaction domain = new WalletTransaction();
        domain.setWalletId(UUID.fromString(entity.getWalletId()));
        domain.setTripId(entity.getTripId() != null ? UUID.fromString(entity.getTripId()) : null);
        domain.setType(entity.getType());
        domain.setAmount(entity.getAmount());
        domain.setResultingBalance(entity.getResultingBalance());
        return domain;
    }
}
