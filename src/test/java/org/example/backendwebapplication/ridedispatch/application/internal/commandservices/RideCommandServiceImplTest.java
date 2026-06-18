package org.example.backendwebapplication.ridedispatch.application.internal.commandservices;

import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverDetailsDto;
import org.example.backendwebapplication.monetization.interfaces.acl.MonetizationContextFacade;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.Ride;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.RideRequest;
import org.example.backendwebapplication.ridedispatch.domain.model.entities.RideCandidate;
import org.example.backendwebapplication.ridedispatch.domain.model.commands.*;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.CandidateStatus;
import org.example.backendwebapplication.ridedispatch.domain.model.valueobjects.RideStatus;
import org.example.backendwebapplication.ridedispatch.domain.repositories.DriverAvailabilityRepository;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRepository;
import org.example.backendwebapplication.ridedispatch.domain.repositories.RideRequestRepository;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RideCommandServiceImplTest {

    private RideRequestRepository rideRequestRepository;
    private RideRepository rideRepository;
    private DriverAvailabilityRepository driverAvailabilityRepository;
    private DriverContextFacade driverContextFacade;
    private MonetizationContextFacade monetizationContextFacade;
    private RideCommandServiceImpl rideCommandService;

    @BeforeEach
    void setUp() {
        rideRequestRepository = mock(RideRequestRepository.class);
        rideRepository = mock(RideRepository.class);
        driverAvailabilityRepository = mock(DriverAvailabilityRepository.class);
        driverContextFacade = mock(DriverContextFacade.class);
        monetizationContextFacade = mock(MonetizationContextFacade.class);

        rideCommandService = new RideCommandServiceImpl(
                rideRequestRepository,
                rideRepository,
                driverAvailabilityRepository,
                driverContextFacade,
                monetizationContextFacade
        );
    }

    @Test
    void createRideRequest_WithValidData_ReturnsSuccess() {
        UUID passengerId = UUID.randomUUID();
        CreateRideRequestCommand command = new CreateRideRequestCommand(
                passengerId,
                "-9.47114,-78.30307",
                "-9.47219,-78.29879",
                1.5,
                5.0
        );

        when(monetizationContextFacade.getMinimumFare()).thenReturn(BigDecimal.valueOf(3.0));
        when(rideRequestRepository.findOpenRequestByPassengerId(passengerId)).thenReturn(Optional.empty());
        when(rideRequestRepository.save(any(RideRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        Result<RideRequest, ApplicationError> result = rideCommandService.handle(command);

        assertTrue(result.isSuccess());
        RideRequest request = result.success().get();
        assertEquals(passengerId, request.getPassengerId());
        assertEquals("-9.47114,-78.30307", request.getOrigin());
        assertEquals(RideStatus.OPEN, request.getStatus());
        verify(rideRequestRepository).save(any(RideRequest.class));
    }

    @Test
    void createRideRequest_WithOpenRequestExists_ReturnsAlreadyHasOpenRequest() {
        UUID passengerId = UUID.randomUUID();
        CreateRideRequestCommand command = new CreateRideRequestCommand(
                passengerId,
                "-9.47114,-78.30307",
                "-9.47219,-78.29879",
                1.5,
                5.0
        );

        when(monetizationContextFacade.getMinimumFare()).thenReturn(BigDecimal.valueOf(3.0));
        RideRequest openReq = new RideRequest(passengerId, "-9.47114,-78.30307", "-9.47219,-78.29879", 1.5, 5.0);
        when(rideRequestRepository.findOpenRequestByPassengerId(passengerId)).thenReturn(Optional.of(openReq));

        Result<RideRequest, ApplicationError> result = rideCommandService.handle(command);

        assertTrue(result.isFailure());
        assertEquals("ALREADY_HAS_OPEN_REQUEST", result.failure().get().code());
    }

    @Test
    void createRideRequest_WithFareBelowMinimum_ReturnsValidationError() {
        UUID passengerId = UUID.randomUUID();
        CreateRideRequestCommand command = new CreateRideRequestCommand(
                passengerId,
                "-9.47114,-78.30307",
                "-9.47219,-78.29879",
                1.5,
                2.5
        );

        when(monetizationContextFacade.getMinimumFare()).thenReturn(BigDecimal.valueOf(3.0));

        Result<RideRequest, ApplicationError> result = rideCommandService.handle(command);

        assertTrue(result.isFailure());
        assertEquals("VALIDATION_ERROR", result.failure().get().code());
        assertTrue(result.failure().get().message().contains("tarifa mínima"));
    }

    @Test
    void applyAsCandidate_WhenDriverRestricted_ReturnsDriverNotAvailable() {
        UUID requestId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        ApplyAsCandidateCommand command = new ApplyAsCandidateCommand(requestId, driverId);

        RideRequest request = new RideRequest(UUID.randomUUID(), "-9.4,-78.3", "-9.4,-78.2", 1.0, 4.0);
        when(rideRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        DriverAvailability availability = new DriverAvailability(driverId);
        availability.syncAvailability(true); // available
        when(driverAvailabilityRepository.findByDriverId(driverId)).thenReturn(Optional.of(availability));

        // driver restricted
        when(driverContextFacade.isDriverRestricted(driverId)).thenReturn(true);

        Result<RideCandidate, ApplicationError> result = rideCommandService.handle(command);

        assertTrue(result.isFailure());
        assertEquals("DRIVER_NOT_AVAILABLE", result.failure().get().code());
    }

    @Test
    void selectCandidate_WhenSuccessful_ReturnsRide() {
        UUID requestId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        SelectCandidateCommand command = new SelectCandidateCommand(requestId, passengerId, candidateId);

        RideRequest request = new RideRequest(requestId, passengerId, null, "-9.4,-78.3", "-9.4,-78.2", 1.0, 4.0, RideStatus.OPEN, false, null, null);
        RideCandidate candidate = new RideCandidate(candidateId, requestId, driverId, "John Doe", "Mototaxi", 4.5, "url", CandidateStatus.PROPOSED, null);
        request.getCandidates().add(candidate);

        when(rideRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        DriverAvailability availability = new DriverAvailability(driverId);
        when(driverAvailabilityRepository.findByDriverId(driverId)).thenReturn(Optional.of(availability));

        Result<Ride, ApplicationError> result = rideCommandService.handle(command);

        assertTrue(result.isSuccess());
        Ride ride = result.success().get();
        assertEquals(requestId, ride.getRequestId());
        assertEquals(driverId, ride.getDriverId());
        assertEquals(RideStatus.ACCEPTED, ride.getStatus());

        assertTrue(availability.isBusy());
        assertEquals(ride.getId(), availability.getActiveRideId());
        verify(driverAvailabilityRepository).save(availability);
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void advanceRideStatus_WhenSuccessful_ReturnsAdvancedRide() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        AdvanceRideStatusCommand command = new AdvanceRideStatusCommand(rideId, driverId, "DRIVER_ON_THE_WAY");

        Ride ride = new Ride(UUID.randomUUID(), UUID.randomUUID(), driverId, "-9.4,-78.3", "-9.4,-78.2", 4.0);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Result<Ride, ApplicationError> result = rideCommandService.handle(command);

        assertTrue(result.isSuccess());
        assertEquals(RideStatus.DRIVER_ON_THE_WAY, result.success().get().getStatus());
    }

    @Test
    void cancelRide_WhenSuccessful_FreesDriver() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        UUID passengerId = UUID.randomUUID();
        CancelRideCommand command = new CancelRideCommand(rideId, passengerId);

        Ride ride = new Ride(UUID.randomUUID(), passengerId, driverId, "-9.4,-78.3", "-9.4,-78.2", 4.0);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        DriverAvailability availability = new DriverAvailability(driverId);
        availability.assignRide(rideId);
        when(driverAvailabilityRepository.findByDriverId(driverId)).thenReturn(Optional.of(availability));

        Result<Ride, ApplicationError> result = rideCommandService.handle(command);

        assertTrue(result.isSuccess());
        assertEquals(RideStatus.CANCELLED, result.success().get().getStatus());
        assertFalse(availability.isBusy());
        assertNull(availability.getActiveRideId());
        verify(driverAvailabilityRepository).save(availability);
    }
}
