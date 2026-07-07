package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.domain.repositories.DriverAvailabilityRepository;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.assemblers.DriverAvailabilityPersistenceAssembler;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.DriverAvailabilityPersistenceEntity;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.repositories.DriverAvailabilityJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DriverAvailabilityRepositoryImpl implements DriverAvailabilityRepository {

    private final DriverAvailabilityJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DriverAvailabilityRepositoryImpl(DriverAvailabilityJpaRepository jpaRepository,
                                            ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public DriverAvailability save(DriverAvailability driverAvailability) {
        var existing = jpaRepository.findByDriverAvailabilityId(driverAvailability.getId().toString())
                .orElse(null);

        DriverAvailabilityPersistenceEntity entity = DriverAvailabilityPersistenceAssembler.toEntity(driverAvailability);

        if (existing != null) {
            entity.setId(existing.getId());
        }

        DriverAvailabilityPersistenceEntity saved = jpaRepository.save(entity);

        driverAvailability.domainEvents().forEach(eventPublisher::publishEvent);
        driverAvailability.clearDomainEvents();

        return DriverAvailabilityPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<DriverAvailability> findByDriverId(UUID driverId) {
        return jpaRepository.findByDriverId(driverId.toString())
                .map(DriverAvailabilityPersistenceAssembler::toDomain);
    }
}
