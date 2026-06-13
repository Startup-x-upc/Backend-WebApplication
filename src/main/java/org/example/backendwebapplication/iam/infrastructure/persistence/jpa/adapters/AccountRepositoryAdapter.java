package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.domain.repositories.AccountRepository;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.assemblers.AccountPersistenceAssembler;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements the domain {@link AccountRepository} port
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository jpa;

    @Override
    public Optional<Account> findById(Long id) {
        return jpa.findById(id)
                .map(AccountPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Account> findByEmail(EmailAddress email) {
        return jpa.findByEmail(email)
                .map(AccountPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Account> findAll() {
        return jpa.findAll().stream()
                .map(AccountPersistenceAssembler::toDomainFromPersistence)
                .collect(Collectors.toList());
    }

    @Override
    public Account save(Account account) {
        var entity = AccountPersistenceAssembler.toPersistenceFromDomain(account);
        var saved  = jpa.save(entity);
        return AccountPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return jpa.existsByEmail(email);
    }
}
