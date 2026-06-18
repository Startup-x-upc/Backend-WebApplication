package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRepository;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.assemblers.RidePersistenceAssembler;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RidePersistenceEntity;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.repositories.RideJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RideRepositoryImpl implements RideRepository {

    private final RideJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RideRepositoryImpl(RideJpaRepository jpaRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Ride save(Ride ride) {
        var existing = jpaRepository.findByRideId(ride.getId().toString())
                .orElse(null);

        RidePersistenceEntity entity = RidePersistenceAssembler.toEntity(ride);

        if (existing != null) {
            entity.setId(existing.getId());
        }

        RidePersistenceEntity saved = jpaRepository.save(entity);

        ride.domainEvents().forEach(eventPublisher::publishEvent);
        ride.clearDomainEvents();

        return RidePersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Ride> findById(UUID id) {
        return jpaRepository.findByRideId(id.toString())
                .map(RidePersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Ride> findActiveRideByDriverId(UUID driverId) {
        var inactiveStatuses = List.of(RideStatus.COMPLETED, RideStatus.CANCELLED);
        return jpaRepository.findByDriverIdAndStatusNotIn(driverId.toString(), inactiveStatuses)
                .map(RidePersistenceAssembler::toDomain);
    }

    @Override
    public List<Ride> findByPassengerId(UUID passengerId, int page, int perPage, RideStatus status) {
        var pageable = PageRequest.of(page, perPage);
        if (status != null) {
            return jpaRepository.findByPassengerIdAndStatus(passengerId.toString(), status, pageable).stream()
                    .map(RidePersistenceAssembler::toDomain)
                    .collect(Collectors.toList());
        }
        return jpaRepository.findByPassengerId(passengerId.toString(), pageable).stream()
                .map(RidePersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByPassengerId(UUID passengerId, RideStatus status) {
        if (status != null) {
            return jpaRepository.countByPassengerIdAndStatus(passengerId.toString(), status);
        }
        return jpaRepository.countByPassengerId(passengerId.toString());
    }

    @Override
    public List<Ride> findByDriverId(UUID driverId, int page, int perPage, RideStatus status) {
        var pageable = PageRequest.of(page, perPage);
        if (status != null) {
            return jpaRepository.findByDriverIdAndStatus(driverId.toString(), status, pageable).stream()
                    .map(RidePersistenceAssembler::toDomain)
                    .collect(Collectors.toList());
        }
        return jpaRepository.findByDriverId(driverId.toString(), pageable).stream()
                .map(RidePersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByDriverId(UUID driverId, RideStatus status) {
        if (status != null) {
            return jpaRepository.countByDriverIdAndStatus(driverId.toString(), status);
        }
        return jpaRepository.countByDriverId(driverId.toString());
    }
}
