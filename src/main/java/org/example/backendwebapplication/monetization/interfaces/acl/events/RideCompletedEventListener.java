package org.example.backendwebapplication.monetization.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.example.backendwebapplication.monetization.application.commandservices.MonetizationCommandService;
import org.example.backendwebapplication.monetization.domain.model.commands.ApplyRideCommissionCommand;
import org.example.backendwebapplication.monetization.domain.repositories.WalletRepository;
import org.example.backendwebapplication.ridedispatch.domain.model.events.RideCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Event listener that implements the ACL for Monetization Bounded Context.
 * Listens to {@link RideCompletedEvent} and applies ride commissions to the driver's wallet.
 */
@Component("monetizationRideCompletedEventListener")
public class RideCompletedEventListener {

    private static final Logger log = LoggerFactory.getLogger(RideCompletedEventListener.class);
    private final MonetizationCommandService commandService;
    private final WalletRepository walletRepository;
    private final DriverContextFacade driverContextFacade;

    public RideCompletedEventListener(MonetizationCommandService commandService,
                                      WalletRepository walletRepository,
                                      DriverContextFacade driverContextFacade) {
        this.commandService = commandService;
        this.walletRepository = walletRepository;
        this.driverContextFacade = driverContextFacade;
    }

    @EventListener
    public void onRideCompleted(RideCompletedEvent event) {
        log.info("RideCompletedEvent received in monetization context. Ride ID: {}, Driver ID: {}, Fare: {}",
                event.rideId(), event.driverId(), event.estimatedFare());
        
        try {
            // Find driver's IAM user ID to locate their wallet
            UUID userId = driverContextFacade.getUserIdByDriverId(event.driverId())
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found in Driver Management context"));
            
            // Find driver's wallet using the user ID
            var wallet = walletRepository.findByDriverId(userId)
                    .orElseThrow(() -> new IllegalStateException("Wallet not found for driver user ID: " + userId));

            // Apply ride commission
            var command = new ApplyRideCommissionCommand(wallet.getWalletId(), event.rideId(), event.estimatedFare());
            commandService.handle(command);
            log.info("Successfully applied commission for ride {} on wallet {}", event.rideId(), wallet.getWalletId());
        } catch (Exception e) {
            log.error("Failed to apply ride commission for ride {} due to: {}", event.rideId(), e.getMessage());
        }
    }
}
