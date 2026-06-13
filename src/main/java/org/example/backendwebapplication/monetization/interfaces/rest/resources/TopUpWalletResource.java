package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record TopUpWalletResource(
        UUID driverId,
        BigDecimal amount
) {}