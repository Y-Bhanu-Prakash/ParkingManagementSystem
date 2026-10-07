package com.parking.entity;

import java.time.LocalDateTime;

public class ParkingSlot {

    public enum Status {
        AVAILABLE,
        OCCUPIED,
        RESERVED,
        OVERDUE,
        MAINTENANCE
    }

    private Long id;
    private String slotCode;
    private VehicleType slotType;
    private Status status;
    private boolean active;
    private LocalDateTime createdAt;

    public ParkingSlot() {
    }

    public ParkingSlot(
            Long id,
            String slotCode,
            VehicleType slotType,
            Status status,
            boolean active,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.slotCode = slotCode;
        this.slotType = slotType;
        this.status = status;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public VehicleType getSlotType() {
        return slotType;
    }

    public void setSlotType(VehicleType slotType) {
        this.slotType = slotType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}