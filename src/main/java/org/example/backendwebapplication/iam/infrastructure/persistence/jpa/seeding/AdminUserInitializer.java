package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.seeding;

import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.model.valueobjects.FullName;
import org.example.backendwebapplication.iam.domain.model.valueobjects.PasswordHash;
import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import org.example.backendwebapplication.iam.domain.repositories.ProfileRepository;
import org.example.backendwebapplication.iam.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initializer to seed a default administrator user in the system.
 * Executed once the application is ready.
 * @author Jesús Iván Castillo Vidal
 */
@Component
public class AdminUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);
    
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public AdminUserInitializer(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Seeds the system administrator user if not already present.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void seedAdminUser() {
        Email adminEmail = new Email("admin@gmail.com");
        
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("No Admin user found in database. Seeding administrator...");
            
            // Create user aggregate with ADMIN role
            User admin = new User(
                    adminEmail,
                    new PasswordHash("admin123"),
                    UserRole.ADMIN
            );
            User savedAdmin = userRepository.save(admin);
            
            // Create subordinate Profile entity
            Profile profile = new Profile(
                    savedAdmin.getUserId(),
                    new FullName("System Administrator"),
                    ""
            );
            profileRepository.save(profile);
            
            log.info("Administrator user successfully seeded (email: admin@gmail.com, password: admin123).");
        } else {
            log.info("Admin user already exists. Seeding skipped.");
        }
    }
}
