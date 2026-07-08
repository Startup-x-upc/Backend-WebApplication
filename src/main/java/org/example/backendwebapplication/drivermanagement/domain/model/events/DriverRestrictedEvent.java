package org.example.backendwebapplication.drivermanagement.domain.model.events;

import java.util.UUID;

/**
 * Event published when a driver is restricted/disabled by administration.
 */
public record DriverRestrictedEvent(UUID driverId, String reason) {}
