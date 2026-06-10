package org.example.backendwebapplication.iam.domain.repositories;

import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for {@link Account} persistence operations.
 * Implemented in the infrastructure layer.
 */
public interface AccountRepository {

    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(EmailAddress email);

    List<Account> findAll();

    Account save(Account account);

    boolean existsByEmail(EmailAddress email);
}
