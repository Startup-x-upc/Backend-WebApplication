package org.example.backendwebapplication.drivermanagement.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.application.commandservices.DriverCommandService;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.UpdateDriverProfileCommand;
import org.example.backendwebapplication.iam.domain.model.events.ProfileUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that handles {@link ProfileUpdatedEvent} by updating driver display details.
 */
@Component("drivermanagementProfileUpdatedEventListener")
public class ProfileUpdatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProfileUpdatedEventListener.class);
    private final DriverCommandService commandService;

    public ProfileUpdatedEventListener(DriverCommandService commandService) {
        this.commandService = commandService;
    }

    @EventListener
    public void onProfileUpdated(ProfileUpdatedEvent event) {
        log.info("ProfileUpdatedEvent received for userId: {}. Syncing driver details...", event.userId());
        var command = new UpdateDriverProfileCommand(
                event.userId(),
                event.fullName(),
                event.photoUrl()
        );
        commandService.handle(command);
    }
}
