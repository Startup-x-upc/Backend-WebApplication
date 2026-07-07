package org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.adapters;

import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRequestRepository;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.assemblers.RideRequestPersistenceAssembler;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.entities.RideRequestPersistenceEntity;
import org.example.backendwebapplication.ridedispatch.infrastructure.persistence.jpa.repositories.RideRequestJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RideRequestRepositoryImpl implements RideRequestRepository {

    private final RideRequestJpaRepository jpaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final long timeoutSeconds;

    public RideRequestRepositoryImpl(RideRequestJpaRepository jpaRepository,
                                     ApplicationEventPublisher eventPublisher,
                                     @Value("${app.ride.request-timeout-seconds:300}") long timeoutSeconds) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
        this.timeoutSeconds = timeoutSeconds;
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
        var opt = jpaRepository.findByRideRequestId(id.toString())
                .map(RideRequestPersistenceAssembler::toDomain);
        if (opt.isPresent()) {
            var request = opt.get();
            if (shouldExpire(request)) {
                request.expire();
                save(request);
            }
        }
        return opt;
    }

    @Override
    public Optional<RideRequest> findOpenRequestByPassengerId(UUID passengerId) {
        var opt = jpaRepository.findFirstByPassengerIdAndStatusAndIsExpiredFalse(passengerId.toString(), RideStatus.OPEN)
                .map(RideRequestPersistenceAssembler::toDomain);
        if (opt.isPresent()) {
            var request = opt.get();
            if (shouldExpire(request)) {
                request.expire();
                save(request);
                return Optional.empty();
            }
        }
        return opt;
    }

    @Override
    public List<RideRequest> findAllByStatus(RideStatus status) {
        var list = jpaRepository.findAllByStatus(status).stream()
                .map(RideRequestPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
        List<RideRequest> result = new ArrayList<>();
        for (var request : list) {
            if (status == RideStatus.OPEN && shouldExpire(request)) {
                request.expire();
                save(request);
            } else {
                result.add(request);
            }
        }
        return result;
    }

    private boolean shouldExpire(RideRequest request) {
        if (request.getStatus() == RideStatus.OPEN && !request.isExpired()) {
            Instant limit = request.getCreatedAt().plusSeconds(timeoutSeconds);
            return Instant.now().isAfter(limit);
        }
        return false;
    }
}

