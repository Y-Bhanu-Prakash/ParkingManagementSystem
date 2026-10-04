package com.parking;

import com.parking.entity.User;
import com.parking.repository.UserRepository;
import com.parking.service.StaffAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StaffAuthenticationControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffAuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        User user = new User(
                2L,
                "Inactive Attendant",
                "inactive",
                "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
                User.Role.ATTENDANT,
                false,
                LocalDateTime.now()
        );

        userRepository.save(user);
    }

    @Test
    void inactiveUserCannotLogin() {
        assertThrows(
                IllegalStateException.class,
                () -> authenticationService.authenticate(
                        "inactive",
                        "password"
                )
        );
    }
}