package org.example.backendwebapplication.drivermanagement.domain.model.commands;

import java.util.UUID;

public record RestrictDriverCommand(UUID driverId, String reason) {}
