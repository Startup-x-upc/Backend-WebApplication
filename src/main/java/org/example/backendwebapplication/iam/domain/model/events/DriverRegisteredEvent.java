package org.example.backendwebapplication.iam.domain.model.events;

import java.util.UUID;

/**
 * Integration event emitted when a new DRIVER user is registered.
 * <p>Consumed by:</p>
 * <ul>
 *   <li><b>Driver Management</b> — creates the Driver entity</li>
 *   <li><b>Monetization</b> — creates the Wallet entity</li>
 * </ul>
 *
 * @param userId        the newly created user's UUID
 * @param email         the driver's email
 * @param fullName      the driver's display name
 * @param vehicleType   the vehicle type
 * @param licenseNumber the driver's license number
 * @param soatNumber    the SOAT insurance number
 */
public record DriverRegisteredEvent(
        UUID userId,
        String email,
        String fullName,
        String vehicleType,
        String licenseNumber,
        String soatNumber) {
}
