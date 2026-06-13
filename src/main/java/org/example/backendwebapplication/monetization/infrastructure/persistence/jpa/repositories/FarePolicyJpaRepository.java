package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.repositories;

import org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.entities.FarePolicyPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarePolicyJpaRepository extends JpaRepository<FarePolicyPersistenceEntity, Long> {
}