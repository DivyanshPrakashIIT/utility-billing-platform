package com.billing.platform.config;

import com.billing.platform.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(AppUserRepository appUserRepository,
                                PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        appUserRepository.findByUsername("admin").ifPresent(admin -> {
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            appUserRepository.save(admin);
            System.out.println("Admin password reset successfully.");
        });
    }
}