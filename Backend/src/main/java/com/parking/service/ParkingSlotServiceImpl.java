package com.parking.service;

import com.parking.entity.ParkingSlot;
import com.parking.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingSlotServiceImpl implements ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;

    public ParkingSlotServiceImpl(ParkingSlotRepository parkingSlotRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
    }

    @Override
    public ParkingSlot createSlot(ParkingSlot slot) {

        // Slot code is required
        if (slot.getSlotCode() == null || slot.getSlotCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Slot code is required");
        }

        // Slot type is required
        if (slot.getSlotType() == null) {
            throw new IllegalArgumentException("Slot type is required");
        }

        // Slot code must be unique
        if (parkingSlotRepository.existsBySlotCode(slot.getSlotCode())) {
            throw new IllegalArgumentException("Slot code already exists");
        }

        // New slots start as AVAILABLE
        slot.setStatus(ParkingSlot.Status.AVAILABLE);

        // New slots are active
        slot.setActive(true);

        // Set creation time
        slot.setCreatedAt(LocalDateTime.now());

        return parkingSlotRepository.save(slot);
    }

    @Override
    public List<ParkingSlot> getAllSlots() {
        return parkingSlotRepository.findAll();
    }

    @Override
    public ParkingSlot getSlotById(Long id) {
        return parkingSlotRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Parking slot not found with ID: " + id));
    }

    @Override
    public ParkingSlot updateSlot(Long id, ParkingSlot slot) {

        ParkingSlot existingSlot = getSlotById(id);

        if (slot.getSlotCode() == null || slot.getSlotCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Slot code is required");
        }

        if (slot.getSlotType() == null) {
            throw new IllegalArgumentException("Slot type is required");
        }

        // Check whether another slot already uses this code
        ParkingSlot slotWithSameCode =
                parkingSlotRepository.findBySlotCode(slot.getSlotCode()).orElse(null);

        if (slotWithSameCode != null
                && !slotWithSameCode.getId().equals(id)) {
            throw new IllegalArgumentException("Slot code already exists");
        }

        existingSlot.setSlotCode(slot.getSlotCode());
        existingSlot.setSlotType(slot.getSlotType());

        return parkingSlotRepository.update(existingSlot);
    }

    @Override
    public ParkingSlot changeStatus(Long id, ParkingSlot.Status status) {

        ParkingSlot slot = getSlotById(id);

        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        // Cannot change an occupied slot to maintenance
        if (status == ParkingSlot.Status.MAINTENANCE
                && slot.getStatus() == ParkingSlot.Status.OCCUPIED) {
            throw new IllegalStateException(
                    "Cannot change slot to MAINTENANCE while it has an active parking session");
        }

        // Do not allow invalid status changes
        if (slot.getStatus() == status) {
            throw new IllegalStateException(
                    "Slot is already in " + status + " status");
        }

        slot.setStatus(status);

        return parkingSlotRepository.update(slot);
    }

    @Override
    public ParkingSlot activateSlot(Long id) {

        ParkingSlot slot = getSlotById(id);

        if (slot.isActive()) {
            throw new IllegalStateException("Parking slot is already active");
        }

        slot.setActive(true);

        return parkingSlotRepository.update(slot);
    }

    @Override
    public ParkingSlot deactivateSlot(Long id) {

        ParkingSlot slot = getSlotById(id);

        // OCCUPIED means there is an active parking session
        if (slot.getStatus() == ParkingSlot.Status.OCCUPIED) {
            throw new IllegalStateException(
                    "Cannot deactivate slot while it has an active parking session");
        }

        if (!slot.isActive()) {
            throw new IllegalStateException("Parking slot is already inactive");
        }

        slot.setActive(false);

        return parkingSlotRepository.update(slot);
    }
}