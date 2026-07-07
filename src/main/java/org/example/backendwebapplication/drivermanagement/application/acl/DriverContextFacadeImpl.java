package org.example.backendwebapplication.drivermanagement.application.acl;

import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverDetailsDto;
import org.example.backendwebapplication.drivermanagement.application.queryservices.DriverQueryService;
import org.example.backendwebapplication.drivermanagement.domain.model.queries.GetDriverByIdQuery;
import org.example.backendwebapplication.drivermanagement.domain.model.queries.GetDriverByUserIdQuery;
import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Concrete implementation of the {@link DriverContextFacade}.
 * Routes requests to internal services in the Driver Management bounded context.
 */
@Component
public class DriverContextFacadeImpl implements DriverContextFacade {

    private final DriverQueryService queryService;

    public DriverContextFacadeImpl(DriverQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public boolean isDriverRestricted(UUID driverId) {
        return queryService.handle(new GetDriverByIdQuery(driverId))
                .map(driver -> driver.getAccessStatus() == DriverAccessStatus.RESTRICTED)
                .orElse(false);
    }

    @Override
    public Optional<UUID> getUserIdByDriverId(UUID driverId) {
        return queryService.handle(new GetDriverByIdQuery(driverId))
                .map(driver -> driver.getUserId());
    }

    @Override
    public Optional<DriverDetailsDto> getDriverDetails(UUID driverId) {
        return queryService.handle(new GetDriverByIdQuery(driverId))
                .map(driver -> new DriverDetailsDto(
                        driver.getFullName(),
                        driver.getVehicleType(),
                        driver.getRatingAverage(),
                        driver.getPhotoUrl()
                ));
    }

    @Override
    public Optional<UUID> getDriverIdByUserId(UUID userId) {
        return queryService.handle(new GetDriverByUserIdQuery(userId))
                .map(driver -> driver.getDriverId());
    }
}
