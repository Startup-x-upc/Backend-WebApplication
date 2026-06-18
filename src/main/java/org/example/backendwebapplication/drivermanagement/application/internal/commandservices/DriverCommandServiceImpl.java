package org.example.backendwebapplication.drivermanagement.application.internal.commandservices;

import org.example.backendwebapplication.drivermanagement.application.commandservices.DriverCommandService;
import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.*;
import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;
import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.example.backendwebapplication.monetization.interfaces.acl.MonetizationContextFacade;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link DriverCommandService}.
 */
@Service
public class DriverCommandServiceImpl implements DriverCommandService {

    private final DriverRepository driverRepository;
    private final MonetizationContextFacade monetizationContextFacade;

    public DriverCommandServiceImpl(DriverRepository driverRepository,
                                    MonetizationContextFacade monetizationContextFacade) {
        this.driverRepository = driverRepository;
        this.monetizationContextFacade = monetizationContextFacade;
    }

    @Override
    @Transactional
    public Result<Driver, ApplicationError> handle(CreateDriverCommand command) {
        var existing = driverRepository.findByUserId(command.userId());
        if (existing.isPresent()) {
            return Result.failure(new ApplicationError("DRIVER_ALREADY_EXISTS", "A driver record already exists for this user"));
        }
        
        Driver driver = new Driver(
                command.userId(),
                command.fullName(),
                command.vehicleType(),
                command.licenseNumber(),
                command.soatNumber()
        );
        Driver saved = driverRepository.save(driver);
        return Result.success(saved);
    }

    @Override
    @Transactional
    public Result<Driver, ApplicationError> handle(ToggleDriverAvailabilityCommand command) {
        Driver driver = driverRepository.findByDriverId(command.driverId())
                .orElse(null);
        if (driver == null) {
            return Result.failure(ApplicationError.notFound("Driver", command.driverId().toString()));
        }

        if (driver.getAccessStatus() == DriverAccessStatus.RESTRICTED) {
            return Result.failure(new ApplicationError("DRIVER_RESTRICTED", "No puedes activarte, cuenta restringida"));
        }

        if (driver.isBusy()) {
            return Result.failure(new ApplicationError("ALREADY_BUSY", "Tienes un ride activo, no puedes cambiar disponibilidad"));
        }

        // Turning availability ON requires positive wallet balance
        if (!driver.isAvailable()) {
            boolean hasBalance = monetizationContextFacade.hasDriverPositiveWalletBalance(driver.getUserId());
            if (!hasBalance) {
                return Result.failure(new ApplicationError("INSUFFICIENT_BALANCE", "Saldo insuficiente para activarse"));
            }
        }

        driver.toggleAvailability();
        Driver saved = driverRepository.save(driver);
        return Result.success(saved);
    }

    @Override
    @Transactional
    public Result<Driver, ApplicationError> handle(RestrictDriverCommand command) {
        Driver driver = driverRepository.findByDriverId(command.driverId())
                .orElse(null);
        if (driver == null) {
            return Result.failure(ApplicationError.notFound("Driver", command.driverId().toString()));
        }

        driver.restrict(command.reason());
        Driver saved = driverRepository.save(driver);
        return Result.success(saved);
    }

    @Override
    @Transactional
    public Result<Driver, ApplicationError> handle(UnrestrictDriverCommand command) {
        Driver driver = driverRepository.findByDriverId(command.driverId())
                .orElse(null);
        if (driver == null) {
            return Result.failure(ApplicationError.notFound("Driver", command.driverId().toString()));
        }

        driver.unrestrict();
        Driver saved = driverRepository.save(driver);
        return Result.success(saved);
    }

    @Override
    @Transactional
    public Result<Driver, ApplicationError> handle(UpdateDriverProfileCommand command) {
        Driver driver = driverRepository.findByUserId(command.userId())
                .orElse(null);
        if (driver == null) {
            // If user has no driver record (e.g. is a Passenger), sync is ignored/success
            return Result.success(null);
        }

        driver.updateProfile(command.fullName(), command.photoUrl());
        Driver saved = driverRepository.save(driver);
        return Result.success(saved);
    }
}
