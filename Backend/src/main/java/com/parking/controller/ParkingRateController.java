package com.parking.controller;

import com.parking.dto.ParkingRateResponse;
import com.parking.dto.ParkingRateUpdateRequest;
import com.parking.service.ParkingRateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
public class ParkingRateController {

    private final ParkingRateService parkingRateService;

    public ParkingRateController(ParkingRateService parkingRateService) {
        this.parkingRateService = parkingRateService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingRateResponse>> getRates() {
        return ResponseEntity.ok(
                parkingRateService.getAllRates()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ParkingRateResponse> updateRate(
            @PathVariable Long id,
            @Valid @RequestBody ParkingRateUpdateRequest request
    ) {
        return ResponseEntity.ok(
                parkingRateService.updateRate(id, request)
        );
    }
}