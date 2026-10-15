package com.bloodmatcher.config;

import com.bloodmatcher.common.Role;
import com.bloodmatcher.user.User;
import com.bloodmatcher.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@bloodmatcher.org}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@BloodMatcher2024!}")
    private String adminPassword;

    @Value("${app.admin.name:System Administrator}")
    private String adminName;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.countByRole(Role.ADMIN) == 0) {
            String normalizedEmail = adminEmail.trim().toLowerCase();
            if (!userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                User admin = new User(
                        adminName,
                        normalizedEmail,
                        passwordEncoder.encode(adminPassword),
                        Role.ADMIN,
                        true
                );
                userRepository.save(admin);
                log.info("Initialized default administrator account: {}", normalizedEmail);
            }
        }
    }
}
