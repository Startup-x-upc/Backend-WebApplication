package org.example.backendwebapplication.drivermanagement.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.example.backendwebapplication.monetization.domain.model.events.WalletEmptyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that handles {@link WalletEmptyEvent} by deactivating driver availability.
 */
@Component("drivermanagementWalletEmptyEventListener")
public class WalletEmptyEventListener {

    private static final Logger log = LoggerFactory.getLogger(WalletEmptyEventListener.class);
    private final DriverRepository driverRepository;

    public WalletEmptyEventListener(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @EventListener
    public void onWalletEmpty(WalletEmptyEvent event) {
        log.info("WalletEmptyEvent received for userId: {}. Deactivating availability...", event.driverId());
        driverRepository.findByUserId(event.driverId()).ifPresentOrElse(driver -> {
            if (driver.isAvailable()) {
                driver.forceDeactivateAvailability();
                driverRepository.save(driver);
                log.info("Driver availability successfully deactivated for driverId: {}", driver.getDriverId());
            }
        }, () -> log.warn("No driver found for userId: {}", event.driverId()));
    }
}
