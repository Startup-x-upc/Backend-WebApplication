package org.example.backendwebapplication.drivermanagement.interfaces.acl;

/**
 * Data Transfer Object for driver details snapshot.
 */
public record DriverDetailsDto(
        String fullName,
        String vehicleType,
        double ratingAverage,
        String photoUrl
) {}
