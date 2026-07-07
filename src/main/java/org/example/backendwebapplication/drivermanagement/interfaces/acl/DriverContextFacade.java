package org.example.backendwebapplication.drivermanagement.interfaces.acl;

import java.util.Optional;
import java.util.UUID;

/**
 * Anti-Corruption Layer (ACL) facade for the Driver Management Bounded Context.
 * Allows other bounded contexts to query driver restriction status and profile details.
 */
public interface DriverContextFacade {

    /**
     * Checks if a driver is restricted.
     *
     * @param driverId the driver business identifier
     * @return {@code true} if the driver exists and is restricted, {@code false} otherwise
     */
    boolean isDriverRestricted(UUID driverId);

    /**
     * Gets the associated IAM User ID for a given Driver ID.
     *
     * @param driverId the driver business identifier
     * @return an optional containing the userId if found, empty otherwise
     */
    Optional<UUID> getUserIdByDriverId(UUID driverId);

    /**
     * Gets the profile details snapshot of a driver.
     *
     * @param driverId the driver business identifier
     * @return an optional containing the driver details DTO if found, empty otherwise
     */
    Optional<DriverDetailsDto> getDriverDetails(UUID driverId);

    /**
     * Gets the associated Driver ID for a given IAM User ID.
     *
     * @param userId the user business identifier
     * @return an optional containing the driverId if found, empty otherwise
     */
    Optional<UUID> getDriverIdByUserId(UUID userId);
}
