package com.parking.config;

import com.parking.entity.User;
import com.parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InitialOwnerInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${parking.initial-owner.name}")
    private String ownerName;

    @Value("${parking.initial-owner.username}")
    private String ownerUsername;

    @Value("${parking.initial-owner.password}")
    private String ownerPassword;

    public InitialOwnerInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(ownerUsername)) {
            return;
        }

        User owner = new User(
                1L,
                ownerName,
                ownerUsername,
                passwordEncoder.encode(ownerPassword),
                User.Role.OWNER,
                true,
                LocalDateTime.now()
        );

        userRepository.save(owner);
    }
}