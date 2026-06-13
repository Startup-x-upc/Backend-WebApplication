package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.UserProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link UserProfilePersistenceEntity}.
 */
public interface UserProfileJpaRepository extends JpaRepository<UserProfilePersistenceEntity, Long> {

    Optional<UserProfilePersistenceEntity> findByAccountId(Long accountId);
}
