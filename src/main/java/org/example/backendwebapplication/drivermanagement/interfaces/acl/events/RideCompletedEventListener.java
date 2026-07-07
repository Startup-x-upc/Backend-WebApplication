package org.example.backendwebapplication.drivermanagement.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.example.backendwebapplication.ridedispatch.domain.model.events.RideCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that handles {@link RideCompletedEvent} by clearing the driver's busy status.
 */
@Component("drivermanagementRideCompletedEventListener")
public class RideCompletedEventListener {

    private static final Logger log = LoggerFactory.getLogger(RideCompletedEventListener.class);
    private final DriverRepository driverRepository;

    public RideCompletedEventListener(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @EventListener
    public void onRideCompleted(RideCompletedEvent event) {
        log.info("RideCompletedEvent received in drivermanagement for rideId: {} by driverId: {}.", event.rideId(), event.driverId());
        driverRepository.findByDriverId(event.driverId()).ifPresentOrElse(driver -> {
            driver.clearBusyStatus();
            driverRepository.save(driver);
            log.info("Driver busy status successfully cleared for driverId: {}", driver.getDriverId());
        }, () -> log.warn("No driver found for driverId: {}", event.driverId()));
    }
}
