package com.parking.controller;

import com.parking.dto.ParkingSlotCreateRequest;
import com.parking.dto.ParkingSlotStatusRequest;
import com.parking.dto.ParkingSlotUpdateRequest;
import com.parking.entity.ParkingSlot;
import com.parking.service.ParkingSlotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slots")
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    public ParkingSlotController(ParkingSlotService parkingSlotService) {
        this.parkingSlotService = parkingSlotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getSlots() {
        return ResponseEntity.ok(
                parkingSlotService.getAllSlots()
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ParkingSlot> createSlot(
            @Valid @RequestBody ParkingSlotCreateRequest request
    ) {

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotCode(request.getSlotCode());
        slot.setSlotType(request.getSlotType());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parkingSlotService.createSlot(slot));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ParkingSlot> updateSlot(
            @PathVariable Long id,
            @Valid @RequestBody ParkingSlotUpdateRequest request
    ) {

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotCode(request.getSlotCode());
        slot.setSlotType(request.getSlotType());

        return ResponseEntity.ok(
                parkingSlotService.updateSlot(id, slot)
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @RequestBody ParkingSlotStatusRequest request
    ) {

        try {
            ParkingSlot slot;

            if (request.getStatus() != null) {
                slot = parkingSlotService.changeStatus(
                        id,
                        request.getStatus()
                );
            } else {
                slot = parkingSlotService.getSlotById(id);
            }

            if (request.getActive() != null) {

                if (request.getActive()) {
                    slot = parkingSlotService.activateSlot(id);
                } else {
                    slot = parkingSlotService.deactivateSlot(id);
                }
            }

            return ResponseEntity.ok(slot);

        } catch (IllegalStateException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            exception.getMessage()
                    ));

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            exception.getMessage()
                    ));
        }
    }
}