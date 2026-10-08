package com.parking.dto;

import com.parking.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ParkingSlotCreateRequest {

    @NotBlank(message = "Slot code is required")
    private String slotCode;

    @NotNull(message = "Slot type is required")
    private VehicleType slotType;

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
}