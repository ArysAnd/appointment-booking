package com.example.appointmentbooking.config;

import com.example.appointmentbooking.user.Role;
import com.example.appointmentbooking.user.User;
import com.example.appointmentbooking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("client@example.com")) {
            User client = User.builder()
                    .fullName("Test Client")
                    .email("client@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.CLIENT)
                    .build();

            userRepository.save(client);
        }

        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = User.builder()
                    .fullName("Admin User")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
        }
    }
}