package org.example.backendwebapplication.drivermanagement.domain.model.valueobjects;

/**
 * Access status representing a driver's clearance to operate on the platform.
 */
public enum DriverAccessStatus {
    ACTIVE,
    PENDING_VERIFICATION,
    RESTRICTED,
    APPROVED,
    REJECTED
}
