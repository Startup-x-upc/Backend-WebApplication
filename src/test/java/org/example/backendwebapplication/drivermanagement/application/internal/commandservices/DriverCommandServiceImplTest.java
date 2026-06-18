package org.example.backendwebapplication.drivermanagement.application.internal.commandservices;

import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.ToggleDriverAvailabilityCommand;
import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.example.backendwebapplication.monetization.interfaces.acl.MonetizationContextFacade;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DriverCommandServiceImplTest {

    private DriverRepository driverRepository;
    private MonetizationContextFacade monetizationContextFacade;
    private DriverCommandServiceImpl driverCommandService;

    @BeforeEach
    void setUp() {
        driverRepository = mock(DriverRepository.class);
        monetizationContextFacade = mock(MonetizationContextFacade.class);
        driverCommandService = new DriverCommandServiceImpl(driverRepository, monetizationContextFacade);
    }

    @Test
    void toggleAvailability_WhenDriverDoesNotExist_ReturnsNotFound() {
        UUID driverId = UUID.randomUUID();
        when(driverRepository.findByDriverId(driverId)).thenReturn(Optional.empty());

        Result<Driver, ApplicationError> result = driverCommandService.handle(new ToggleDriverAvailabilityCommand(driverId));

        assertTrue(result.isFailure());
        assertEquals("DRIVER_NOT_FOUND", result.failure().get().code());
    }

    @Test
    void toggleAvailability_WhenDriverIsRestricted_ReturnsDriverRestricted() {
        UUID driverId = UUID.randomUUID();
        Driver driver = new Driver(UUID.randomUUID(), "John Doe", "Mototaxi", "LIC123", "SOAT123");
        driver.restrict("Reason");
        when(driverRepository.findByDriverId(driverId)).thenReturn(Optional.of(driver));

        Result<Driver, ApplicationError> result = driverCommandService.handle(new ToggleDriverAvailabilityCommand(driverId));

        assertTrue(result.isFailure());
        assertEquals("DRIVER_RESTRICTED", result.failure().get().code());
    }

    @Test
    void toggleAvailability_WhenDriverIsBusy_ReturnsAlreadyBusy() {
        UUID driverId = UUID.randomUUID();
        Driver driver = new Driver(UUID.randomUUID(), "John Doe", "Mototaxi", "LIC123", "SOAT123");
        driver.setBusy(true);
        when(driverRepository.findByDriverId(driverId)).thenReturn(Optional.of(driver));

        Result<Driver, ApplicationError> result = driverCommandService.handle(new ToggleDriverAvailabilityCommand(driverId));

        assertTrue(result.isFailure());
        assertEquals("ALREADY_BUSY", result.failure().get().code());
    }

    @Test
    void toggleAvailability_WhenTurningOnAndInsufficientBalance_ReturnsInsufficientBalance() {
        UUID driverId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Driver driver = new Driver(userId, "John Doe", "Mototaxi", "LIC123", "SOAT123");
        // isAvailable starts as false
        when(driverRepository.findByDriverId(driverId)).thenReturn(Optional.of(driver));
        when(monetizationContextFacade.hasDriverPositiveWalletBalance(userId)).thenReturn(false);

        Result<Driver, ApplicationError> result = driverCommandService.handle(new ToggleDriverAvailabilityCommand(driverId));

        assertTrue(result.isFailure());
        assertEquals("INSUFFICIENT_BALANCE", result.failure().get().code());
    }

    @Test
    void toggleAvailability_WhenTurningOnAndHasBalance_TogglesSuccessfully() {
        UUID driverId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Driver driver = new Driver(userId, "John Doe", "Mototaxi", "LIC123", "SOAT123");
        // isAvailable starts as false
        when(driverRepository.findByDriverId(driverId)).thenReturn(Optional.of(driver));
        when(monetizationContextFacade.hasDriverPositiveWalletBalance(userId)).thenReturn(true);
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Result<Driver, ApplicationError> result = driverCommandService.handle(new ToggleDriverAvailabilityCommand(driverId));

        assertTrue(result.isSuccess());
        assertTrue(result.success().get().isAvailable());
        verify(driverRepository).save(driver);
    }
}
