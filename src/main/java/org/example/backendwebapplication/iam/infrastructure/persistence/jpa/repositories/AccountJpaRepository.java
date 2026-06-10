package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.AccountPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link AccountPersistenceEntity}.
 */
public interface AccountJpaRepository extends JpaRepository<AccountPersistenceEntity, Long> {

    Optional<AccountPersistenceEntity> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);
}
