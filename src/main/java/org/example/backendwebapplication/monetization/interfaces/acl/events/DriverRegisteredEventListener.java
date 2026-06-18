package org.example.backendwebapplication.monetization.interfaces.acl.events;

import org.example.backendwebapplication.iam.domain.model.events.DriverRegisteredEvent;
import org.example.backendwebapplication.monetization.application.commandservices.MonetizationCommandService;
import org.example.backendwebapplication.monetization.domain.model.commands.CreateWalletCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener that implements the ACL for Monetization Bounded Context.
 * Listens to {@link DriverRegisteredEvent} and registers a new Wallet for the driver.
 */
@Component
public class DriverRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(DriverRegisteredEventListener.class);
    private final MonetizationCommandService commandService;

    public DriverRegisteredEventListener(MonetizationCommandService commandService) {
        this.commandService = commandService;
    }

    @EventListener
    public void onDriverRegistered(DriverRegisteredEvent event) {
        log.info("DriverRegisteredEvent received for driverId: {}. Creating wallet...", event.userId());
        try {
            commandService.handle(new CreateWalletCommand(event.userId()));
            log.info("Wallet successfully created for driverId: {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to create wallet for driverId: {} due to: {}", event.userId(), e.getMessage());
        }
    }
}
