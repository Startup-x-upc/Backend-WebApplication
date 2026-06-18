package org.example.backendwebapplication.drivermanagement.domain.model.events;

import java.util.UUID;

/**
 * Event published when a driver toggles their availability status.
 */
public record DriverAvailabilityChangedEvent(UUID driverId, boolean isAvailable) {}
