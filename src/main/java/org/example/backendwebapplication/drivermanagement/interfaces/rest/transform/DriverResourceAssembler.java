package org.example.backendwebapplication.drivermanagement.interfaces.rest.transform;

import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.interfaces.rest.resources.DriverAvailabilityResponse;
import org.example.backendwebapplication.drivermanagement.interfaces.rest.resources.DriverResponse;

/**
 * Assembler class for converting Driver aggregates into REST responses.
 */
public class DriverResourceAssembler {

    public static DriverResponse toResource(Driver driver) {
        if (driver == null) {
            return null;
        }
        return new DriverResponse(
                driver.getDriverId(),
                driver.getUserId(),
                driver.getFullName(),
                driver.getVehicleType(),
                driver.getAccessStatus().name(),
                driver.isAvailable(),
                driver.getRatingAverage(),
                driver.getRatingCount(),
                driver.getPhotoUrl(),
                driver.getLicenseNumber(),
                driver.getSoatNumber(),
                driver.isBusy(),
                driver.getActiveRideId(),
                driver.getRestrictionReason(),
                driver.getCreatedAt()
        );
    }

    public static DriverAvailabilityResponse toAvailabilityResource(Driver driver) {
        if (driver == null) {
            return null;
        }
        return new DriverAvailabilityResponse(
                driver.getDriverId(),
                driver.getUserId(),
                driver.isAvailable(),
                driver.isBusy(),
                driver.getActiveRideId()
        );
    }
}
