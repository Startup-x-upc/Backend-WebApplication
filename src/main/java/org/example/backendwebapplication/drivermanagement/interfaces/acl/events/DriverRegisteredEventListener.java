package org.example.backendwebapplication.drivermanagement.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.application.commandservices.DriverCommandService;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.CreateDriverCommand;
import org.example.backendwebapplication.iam.domain.model.events.DriverRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that handles {@link DriverRegisteredEvent} by creating a new Driver aggregate.
 */
@Component("drivermanagementDriverRegisteredEventListener")
public class DriverRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(DriverRegisteredEventListener.class);
    private final DriverCommandService commandService;

    public DriverRegisteredEventListener(DriverCommandService commandService) {
        this.commandService = commandService;
    }

    @EventListener
    public void onDriverRegistered(DriverRegisteredEvent event) {
        log.info("DriverRegisteredEvent received for userId: {}. Creating driver aggregate...", event.userId());
        var command = new CreateDriverCommand(
                event.userId(),
                event.fullName(),
                event.vehicleType(),
                event.licenseNumber(),
                event.soatNumber()
        );
        var result = commandService.handle(command);
        if (result.isFailure()) {
            log.error("Failed to create driver aggregate for userId: {}. Reason: {}", 
                    event.userId(), result.failure().get().message());
        } else {
            log.info("Driver aggregate successfully created for userId: {}", event.userId());
        }
    }
}
