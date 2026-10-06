package com.parking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaffAuthorizationTestController {

    @GetMapping("/api/owner/test")
    public String ownerEndpoint() {
        return "OWNER access granted";
    }

    @GetMapping("/api/attendant/test")
    public String attendantEndpoint() {
        return "ATTENDANT access granted";
    }
}