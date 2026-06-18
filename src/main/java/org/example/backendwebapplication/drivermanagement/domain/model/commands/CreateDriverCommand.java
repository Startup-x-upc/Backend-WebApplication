package org.example.backendwebapplication.drivermanagement.domain.model.commands;

import java.util.UUID;

public record CreateDriverCommand(
        UUID userId,
        String fullName,
        String vehicleType,
        String licenseNumber,
        String soatNumber
) {}
