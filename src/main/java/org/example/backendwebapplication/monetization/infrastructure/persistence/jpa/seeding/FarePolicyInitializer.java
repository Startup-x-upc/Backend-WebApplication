package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.seeding;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.repositories.FarePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FarePolicyInitializer {

    private static final Logger log = LoggerFactory.getLogger(FarePolicyInitializer.class);
    private final FarePolicyRepository farePolicyRepository;

    public FarePolicyInitializer(FarePolicyRepository farePolicyRepository) {
        this.farePolicyRepository = farePolicyRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedFarePolicy() {
        if (farePolicyRepository.getCurrent().isEmpty()) {
            log.info("No FarePolicy found in database. Seeding default configuration...");
            FarePolicy defaultPolicy = new FarePolicy(
                    new BigDecimal("2.50"),
                    new BigDecimal("1.20"),
                    new BigDecimal("4.00"),
                    new BigDecimal("0.05")
            );
            farePolicyRepository.save(defaultPolicy);
            log.info("Default FarePolicy successfully seeded.");
        } else {
            log.info("FarePolicy already exists. Seeding skipped.");
        }
    }
}
