package org.example.backendwebapplication.drivermanagement.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.example.backendwebapplication.trustreputation.domain.model.events.DriverReputationUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("drivermanagementDriverReputationUpdatedEventListener")
public class DriverReputationUpdatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(DriverReputationUpdatedEventListener.class);

    private final DriverRepository driverRepository;

    public DriverReputationUpdatedEventListener(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @EventListener
    public void onDriverReputationUpdated(DriverReputationUpdatedEvent event) {
        log.info("DriverReputationUpdatedEvent received in drivermanagement. Driver ID: {}, Avg Score: {}, Count: {}",
                event.driverId(), event.averageScore(), event.totalRatings());
        try {
            driverRepository.findByDriverId(event.driverId()).ifPresentOrElse(driver -> {
                driver.setRatingAverage(event.averageScore());
                driver.setRatingCount((int) event.totalRatings());
                driverRepository.save(driver);
                log.info("Successfully updated reputation for driver ID: {}", event.driverId());
            }, () -> {
                log.warn("Driver with ID {} not found in Driver Management context", event.driverId());
            });
        } catch (Exception e) {
            log.error("Error updating reputation for driver ID: {}", event.driverId(), e);
        }
    }
}
