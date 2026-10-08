package com.parking.dto;

import com.parking.entity.ParkingSlot.Status;

public class ParkingSlotStatusRequest {

    private Status status;

    private Boolean active;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}