package org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;
import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.assemblers.DriverPersistenceAssembler;
import org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;
import org.example.backendwebapplication.drivermanagement.infrastructure.persistence.jpa.repositories.DriverJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementation for {@link DriverRepository}.
 */
@Repository
public class DriverRepositoryImpl implements DriverRepository {

    private final DriverJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DriverRepositoryImpl(DriverJpaRepository jpaRepository,
                                ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Driver save(Driver driver) {
        var existing = jpaRepository.findByDriverId(driver.getDriverId().toString())
                .orElse(null);

        DriverPersistenceEntity entity = DriverPersistenceAssembler.toEntity(driver);

        if (existing != null) {
            entity.setId(existing.getId());
        }

        DriverPersistenceEntity saved = jpaRepository.save(entity);

        driver.domainEvents().forEach(eventPublisher::publishEvent);
        driver.clearDomainEvents();

        return DriverPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Driver> findByDriverId(UUID driverId) {
        return jpaRepository.findByDriverId(driverId.toString())
                .map(DriverPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Driver> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId.toString())
                .map(DriverPersistenceAssembler::toDomain);
    }

    @Override
    public List<Driver> findAll(int page, int perPage, DriverAccessStatus accessStatus) {
        var pageable = PageRequest.of(page, perPage);
        if (accessStatus != null) {
            return jpaRepository.findByAccessStatus(accessStatus, pageable).stream()
                    .map(DriverPersistenceAssembler::toDomain)
                    .collect(Collectors.toList());
        }
        return jpaRepository.findAll(pageable).stream()
                .map(DriverPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(DriverAccessStatus accessStatus) {
        if (accessStatus != null) {
            return jpaRepository.countByAccessStatus(accessStatus);
        }
        return jpaRepository.count();
    }
}
