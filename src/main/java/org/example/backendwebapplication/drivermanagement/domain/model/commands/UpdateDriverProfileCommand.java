package org.example.backendwebapplication.drivermanagement.domain.model.commands;

import java.util.UUID;

public record UpdateDriverProfileCommand(
        UUID userId,
        String fullName,
        String photoUrl
) {}
