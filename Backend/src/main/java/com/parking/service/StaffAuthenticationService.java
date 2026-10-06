package com.parking.service;

import com.parking.entity.User;
import com.parking.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StaffAuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffAuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid username or password"
                        )
                );

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "User account is inactive"
            );
        }

        if (!passwordEncoder.matches(
                password,
                user.getPasswordHash()
        )) {
            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }

        return user;
    }
}