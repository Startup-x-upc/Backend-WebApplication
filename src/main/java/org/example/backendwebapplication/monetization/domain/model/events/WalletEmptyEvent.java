package org.example.backendwebapplication.monetization.domain.model.events;

import java.util.UUID;

/**
 * Event published when a wallet's balance is reduced to 0.00.
 * Extends AbstractDomainEvent if required, or is simply a record.
 * In our architecture, integration events or domain events can be record types.
 */
public record WalletEmptyEvent(UUID walletId, UUID driverId) {}
