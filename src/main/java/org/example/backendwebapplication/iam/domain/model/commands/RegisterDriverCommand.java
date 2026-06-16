package org.example.backendwebapplication.iam.domain.model.commands;

/**
 * Command to register a new DRIVER user.
 * <p>Creates User + Profile, then emits {@code DriverRegisteredEvent}
 * so Driver Management and Monetization can create their own entities.</p>
 *
 * @param email         the user's email address
 * @param password      the raw (unhashed) password
 * @param fullName      the driver's display name
 * @param vehicleType   the type of vehicle (e.g. "Mototaxi")
 * @param licenseNumber the driver's license number
 * @param soatNumber    the SOAT insurance number
 */
public record RegisterDriverCommand(
        String email,
        String password,
        String fullName,
        String vehicleType,
        String licenseNumber,
        String soatNumber) {
}
