package com.parking.controller;

import com.parking.dto.StaffLoginRequest;
import com.parking.dto.StaffLoginResponse;
import com.parking.entity.User;
import com.parking.service.StaffAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class StaffAuthenticationController {

    private final StaffAuthenticationService authenticationService;

    public StaffAuthenticationController(
            StaffAuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<StaffLoginResponse> login(
            @Valid @RequestBody StaffLoginRequest request
    ) {
        User user = authenticationService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        StaffLoginResponse response = new StaffLoginResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}