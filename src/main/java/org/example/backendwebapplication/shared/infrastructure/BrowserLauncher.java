package org.example.backendwebapplication.shared.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

/**
 * Opens the Swagger UI in the default browser when the application starts.
 * <p>Development convenience — silently ignores failures (headless servers, etc.).</p>
 */
@Component
public class BrowserLauncher {

    private static final Logger log = LoggerFactory.getLogger(BrowserLauncher.class);
    private static final String SWAGGER_URL = "http://localhost:8080/swagger-ui/index.html";

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(SWAGGER_URL));
                log.info("Swagger UI opened at {}", SWAGGER_URL);
            } else {
                log.info("Swagger UI available at {}", SWAGGER_URL);
            }
        } catch (Exception e) {
            log.info("Swagger UI available at {} (auto-open failed: {})", SWAGGER_URL, e.getMessage());
        }
    }
}
