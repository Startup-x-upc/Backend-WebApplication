package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers;

import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.AccountPersistenceEntity;

/**
 * Assembler that translates between the {@link Account} domain aggregate
 * and its {@link AccountPersistenceEntity} JPA representation.
 */
public final class AccountPersistenceAssembler {

    private AccountPersistenceAssembler() {}

    /**
     * Converts a JPA entity to a domain aggregate.
     */
    public static Account toDomainFromPersistence(AccountPersistenceEntity entity) {
        return new Account(
                entity.getId(),
                entity.getEmail().address(),
                entity.getPasswordHash(),
                entity.getRole());
    }

    /**
     * Converts a domain aggregate to a JPA entity.
     */
    public static AccountPersistenceEntity toPersistenceFromDomain(Account account) {
        var entity = new AccountPersistenceEntity();
        entity.setId(account.getId());
        entity.setEmail(new org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress(account.getEmail()));
        entity.setPasswordHash(account.getPasswordHash());
        entity.setRole(account.getRole());
        return entity;
    }
}
