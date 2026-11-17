package com.sreekar.employeemanagementportal.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.repository.UserRepository;

@Component
public class AdminBootstrap implements ApplicationRunner {

    @Value("${BOOTSTRAP_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${BOOTSTRAP_ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${BOOTSTRAP_ADMIN_NAME:Portal Administrator}")
    private String adminName;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean hasEmail = adminEmail != null && !adminEmail.isBlank();
        boolean hasPassword = adminPassword != null && !adminPassword.isBlank();
        if (!hasEmail && !hasPassword) return;
        if (!hasEmail || !hasPassword) {
            throw new IllegalStateException("Set both BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD to provision the initial administrator.");
        }
        if (adminPassword.getBytes(StandardCharsets.UTF_8).length < 12
                || adminPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD must be 12 to 72 UTF-8 bytes.");
        }

        String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmail(normalizedEmail).ifPresentOrElse(existing -> {
            if (!"ADMIN".equals(existing.getRole())) {
                throw new IllegalStateException("The bootstrap administrator email already belongs to a non-admin account.");
            }
        }, () -> {
            User admin = new User();
            admin.setName(adminName == null || adminName.isBlank() ? "Portal Administrator" : adminName.trim());
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        });
    }
}
