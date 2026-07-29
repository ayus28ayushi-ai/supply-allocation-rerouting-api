package com.triage.dera.config;


import com.triage.dera.entity.AuthProvider;
import com.triage.dera.entity.Role;
import com.triage.dera.entity.Users;
import com.triage.dera.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPass;

    @Override
    public void run(String... args) throws Exception {
        Boolean isAdminThere = userRepo.existsByEmail(adminEmail);
        if (!isAdminThere) {
            Users admin = Users.builder()
                    .username("MainAdmin")
                    .email(adminEmail)
                    .password(passEncoder.encode(adminPass))
                    .provider(AuthProvider.LOCAL)
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepo.save(admin);
            System.out.println("Initial Admin account created successfully for: " + adminEmail);
        }
    }
}
