package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserPersistenceEntity}.
 */
public interface UserPersistenceRepository extends JpaRepository<UserPersistenceEntity, Long> {

    Optional<UserPersistenceEntity> findByUserId(UUID userId);

    Optional<UserPersistenceEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
