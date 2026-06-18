package org.example.backendwebapplication.drivermanagement.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.example.backendwebapplication.ridedispatch.domain.model.events.RideAssignedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that handles {@link RideAssignedEvent} by marking the driver as busy.
 */
@Component("drivermanagementRideAssignedEventListener")
public class RideAssignedEventListener {

    private static final Logger log = LoggerFactory.getLogger(RideAssignedEventListener.class);
    private final DriverRepository driverRepository;

    public RideAssignedEventListener(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @EventListener
    public void onRideAssigned(RideAssignedEvent event) {
        log.info("RideAssignedEvent received for rideId: {} assigned to driverId: {}.", event.rideId(), event.driverId());
        driverRepository.findByDriverId(event.driverId()).ifPresentOrElse(driver -> {
            driver.markAsBusy(event.rideId());
            driverRepository.save(driver);
            log.info("Driver isBusy successfully set to true for driverId: {}", driver.getDriverId());
        }, () -> log.warn("No driver found for driverId: {}", event.driverId()));
    }
}
