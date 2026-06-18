package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRequestRepository;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.assemblers.RideRequestPersistenceAssembler;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RideRequestPersistenceEntity;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.repositories.RideRequestJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RideRequestRepositoryImpl implements RideRequestRepository {

    private final RideRequestJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RideRequestRepositoryImpl(RideRequestJpaRepository jpaRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public RideRequest save(RideRequest rideRequest) {
        var existing = jpaRepository.findByRideRequestId(rideRequest.getId().toString())
                .orElse(null);

        RideRequestPersistenceEntity entity = RideRequestPersistenceAssembler.toEntity(rideRequest);

        if (existing != null) {
            entity.setId(existing.getId());
            // Keep existing candidate primary keys to prevent Hibernate from inserting duplicate rows
            for (var cEntity : entity.getCandidates()) {
                existing.getCandidates().stream()
                        .filter(existC -> existC.getCandidateId().equals(cEntity.getCandidateId()))
                        .findFirst()
                        .ifPresent(existC -> cEntity.setId(existC.getId()));
            }
        }

        RideRequestPersistenceEntity saved = jpaRepository.save(entity);

        rideRequest.domainEvents().forEach(eventPublisher::publishEvent);
        rideRequest.clearDomainEvents();

        return RideRequestPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<RideRequest> findById(UUID id) {
        return jpaRepository.findByRideRequestId(id.toString())
                .map(RideRequestPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<RideRequest> findOpenRequestByPassengerId(UUID passengerId) {
        return jpaRepository.findFirstByPassengerIdAndStatusAndIsExpiredFalse(passengerId.toString(), RideStatus.OPEN)
                .map(RideRequestPersistenceAssembler::toDomain);
    }

    @Override
    public List<RideRequest> findAllByStatus(RideStatus status) {
        return jpaRepository.findAllByStatus(status).stream()
                .map(RideRequestPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }
}
