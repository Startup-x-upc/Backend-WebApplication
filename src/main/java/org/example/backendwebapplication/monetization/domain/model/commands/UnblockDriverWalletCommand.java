package org.example.backendwebapplication.monetization.domain.model.commands;

import java.util.UUID;

public record UnblockDriverWalletCommand(
        UUID driverId
) {}