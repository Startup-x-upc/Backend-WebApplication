package org.example.backendwebapplication.ridedispatch.application.internal.commandservices;

import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.example.backendwebapplication.monetization.interfaces.acl.MonetizationContextFacade;
import org.example.backendwebapplication.ridedispatch.application.commandservices.RideCommandService;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.domain.model.commands.*;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.domain.repositories.DriverAvailabilityRepository;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRepository;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRequestRepository;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RideCommandServiceImpl implements RideCommandService {

    private final RideRequestRepository rideRequestRepository;
    private final RideRepository rideRepository;
    private final DriverAvailabilityRepository driverAvailabilityRepository;
    private final DriverContextFacade driverContextFacade;
    private final MonetizationContextFacade monetizationContextFacade;

    public RideCommandServiceImpl(RideRequestRepository rideRequestRepository,
                                  RideRepository rideRepository,
                                  DriverAvailabilityRepository driverAvailabilityRepository,
                                  DriverContextFacade driverContextFacade,
                                  MonetizationContextFacade monetizationContextFacade) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideRepository = rideRepository;
        this.driverAvailabilityRepository = driverAvailabilityRepository;
        this.driverContextFacade = driverContextFacade;
        this.monetizationContextFacade = monetizationContextFacade;
    }

    @Override
    @Transactional
    public Result<RideRequest, ApplicationError> handle(CreateRideRequestCommand command) {
        // Validation: origin & destination format "lat,lng"
        String latLngRegex = "^-?\\d+(\\.\\d+)?,\\s*-?\\d+(\\.\\d+)?$";
        if (command.origin() == null || !command.origin().matches(latLngRegex) ||
            command.destination() == null || !command.destination().matches(latLngRegex)) {
            return Result.failure(new ApplicationError("VALIDATION_ERROR", "Origen o destino inválido. Deben ser coordenadas lat,lng"));
        }

        if (command.distanceKm() <= 0) {
            return Result.failure(new ApplicationError("VALIDATION_ERROR", "La distancia debe ser mayor a 0"));
        }

        // Validate estimatedFare >= minimumFare
        BigDecimal minFare = monetizationContextFacade.getMinimumFare();
        if (BigDecimal.valueOf(command.estimatedFare()).compareTo(minFare) < 0) {
            return Result.failure(new ApplicationError("VALIDATION_ERROR", "La tarifa estimada es menor a la tarifa mínima (" + minFare + ")"));
        }

        // Passenger cannot have another OPEN request
        var openRequest = rideRequestRepository.findOpenRequestByPassengerId(command.passengerId());
        if (openRequest.isPresent()) {
            return Result.failure(new ApplicationError("ALREADY_HAS_OPEN_REQUEST", "Ya tienes una solicitud abierta"));
        }

        RideRequest request = new RideRequest(
                command.passengerId(),
                command.origin(),
                command.destination(),
                command.distanceKm(),
                command.estimatedFare()
        );

        RideRequest saved = rideRequestRepository.save(request);
        return Result.success(saved);
    }

    @Override
    @Transactional
    public Result<RideCandidate, ApplicationError> handle(ApplyAsCandidateCommand command) {
        RideRequest request = rideRequestRepository.findById(command.requestId())
                .orElse(null);
        if (request == null) {
            return Result.failure(ApplicationError.notFound("RideRequest", command.requestId().toString()));
        }

        if (request.isExpired()) {
            return Result.failure(new ApplicationError("REQUEST_EXPIRED", "La solicitud ya expiró"));
        }

        if (request.getStatus() != RideStatus.OPEN) {
            return Result.failure(new ApplicationError("REQUEST_NOT_OPEN", "La solicitud ya no está abierta"));
        }

        // Verify driver availability and busy state
        DriverAvailability availability = driverAvailabilityRepository.findByDriverId(command.driverId())
                .orElse(null);
        if (availability == null || !availability.isAvailable() || availability.isBusy()) {
            return Result.failure(new ApplicationError("DRIVER_NOT_AVAILABLE", "No estás disponible o estás ocupado"));
        }

        // Verify driver is not restricted
        if (driverContextFacade.isDriverRestricted(command.driverId())) {
            return Result.failure(new ApplicationError("DRIVER_NOT_AVAILABLE", "No puedes aplicar, cuenta restringida"));
        }

        // Check if already applied
        boolean alreadyApplied = request.getCandidates().stream()
                .anyMatch(c -> c.getDriverId().equals(command.driverId()));
        if (alreadyApplied) {
            return Result.failure(new ApplicationError("ALREADY_APPLIED", "Ya aplicaste a este request"));
        }

        // Fetch driver details snapshot
        var details = driverContextFacade.getDriverDetails(command.driverId())
                .orElse(null);
        if (details == null) {
            return Result.failure(ApplicationError.notFound("Driver details for", command.driverId().toString()));
        }

        try {
            RideCandidate candidate = request.applyCandidate(
                    command.driverId(),
                    details.fullName(),
                    details.vehicleType(),
                    details.ratingAverage(),
                    details.photoUrl()
            );

            rideRequestRepository.save(request);
            return Result.success(candidate);
        } catch (IllegalStateException e) {
            return Result.failure(new ApplicationError("BUSINESS_RULE_VIOLATION", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Ride, ApplicationError> handle(SelectCandidateCommand command) {
        RideRequest request = rideRequestRepository.findById(command.requestId())
                .orElse(null);
        if (request == null) {
            return Result.failure(ApplicationError.notFound("RideRequest", command.requestId().toString()));
        }

        // Verify request belongs to the passenger
        if (!request.getPassengerId().equals(command.passengerId())) {
            return Result.failure(new ApplicationError("FORBIDDEN", "No eres el dueño del request"));
        }

        try {
            RideCandidate selected = request.selectCandidate(command.candidateId());
            rideRequestRepository.save(request);

            // Create Ride
            Ride ride = new Ride(
                    request.getId(),
                    request.getPassengerId(),
                    selected.getDriverId(),
                    request.getOrigin(),
                    request.getDestination(),
                    request.getEstimatedFare()
            );
            Ride savedRide = rideRepository.save(ride);

            // Mark Driver Availability as busy
            DriverAvailability availability = driverAvailabilityRepository.findByDriverId(selected.getDriverId())
                    .orElseThrow(() -> new IllegalStateException("Driver availability record not found"));
            availability.assignRide(savedRide.getId());
            driverAvailabilityRepository.save(availability);

            return Result.success(savedRide);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.notFound("RideCandidate", command.candidateId().toString()));
        } catch (IllegalStateException e) {
            return Result.failure(new ApplicationError("REQUEST_NOT_OPEN", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Ride, ApplicationError> handle(AdvanceRideStatusCommand command) {
        Ride ride = rideRepository.findById(command.rideId())
                .orElse(null);
        if (ride == null) {
            return Result.failure(ApplicationError.notFound("Ride", command.rideId().toString()));
        }

        // Verify assigned driver
        if (!ride.getDriverId().equals(command.driverId())) {
            return Result.failure(new ApplicationError("FORBIDDEN", "No eres el conductor asignado"));
        }

        try {
            RideStatus targetStatus = RideStatus.valueOf(command.status().toUpperCase());
            ride.advanceStatus(targetStatus);
            Ride savedRide = rideRepository.save(ride);

            // Side effect on COMPLETED
            if (targetStatus == RideStatus.COMPLETED) {
                var availability = driverAvailabilityRepository.findByDriverId(ride.getDriverId()).orElse(null);
                if (availability != null) {
                    availability.clearRide();
                    driverAvailabilityRepository.save(availability);
                }
            }

            return Result.success(savedRide);
        } catch (IllegalArgumentException e) {
            return Result.failure(new ApplicationError("INVALID_TRANSITION", "Estado de destino inválido"));
        } catch (IllegalStateException e) {
            return Result.failure(new ApplicationError("INVALID_TRANSITION", e.getMessage().replace("INVALID_TRANSITION: ", "")));
        }
    }

    @Override
    @Transactional
    public Result<Ride, ApplicationError> handle(CancelRideCommand command) {
        Ride ride = rideRepository.findById(command.rideId())
                .orElse(null);
        if (ride == null) {
            return Result.failure(ApplicationError.notFound("Ride", command.rideId().toString()));
        }

        // Verify requester is part of this ride
        if (!ride.getPassengerId().equals(command.requesterId()) && !ride.getDriverId().equals(command.requesterId())) {
            return Result.failure(new ApplicationError("FORBIDDEN", "No eres parte de este ride"));
        }

        try {
            ride.cancel();
            Ride savedRide = rideRepository.save(ride);

            // Free Driver Availability
            var availability = driverAvailabilityRepository.findByDriverId(ride.getDriverId()).orElse(null);
            if (availability != null) {
                availability.clearRide();
                driverAvailabilityRepository.save(availability);
            }

            return Result.success(savedRide);
        } catch (IllegalStateException e) {
            return Result.failure(new ApplicationError("CANNOT_CANCEL", e.getMessage().replace("CANNOT_CANCEL: ", "")));
        }
    }

    @Override
    @Transactional
    public Result<RideRequest, ApplicationError> handle(CancelRideRequestCommand command) {
        RideRequest request = rideRequestRepository.findById(command.requestId())
                .orElse(null);
        if (request == null) {
            return Result.failure(ApplicationError.notFound("RideRequest", command.requestId().toString()));
        }

        // Verify request belongs to the passenger
        if (!request.getPassengerId().equals(command.passengerId())) {
            return Result.failure(new ApplicationError("FORBIDDEN", "Solo el pasajero que creó la solicitud puede cancelarla"));
        }

        try {
            request.cancel();
            RideRequest saved = rideRequestRepository.save(request);
            return Result.success(saved);
        } catch (IllegalStateException e) {
            return Result.failure(new ApplicationError("REQUEST_NOT_OPEN", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Void, ApplicationError> handle(WithdrawCandidateCommand command) {
        RideRequest request = rideRequestRepository.findById(command.requestId())
                .orElse(null);
        if (request == null) {
            return Result.failure(ApplicationError.notFound("RideRequest", command.requestId().toString()));
        }

        try {
            request.withdrawCandidate(command.driverId());
            rideRequestRepository.save(request);
            return Result.success(null);
        } catch (IllegalStateException e) {
            return Result.failure(new ApplicationError("REQUEST_NOT_OPEN", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(new ApplicationError("CANDIDATE_NOT_FOUND", e.getMessage()));
        }
    }
}
