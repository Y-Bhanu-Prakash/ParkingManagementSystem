package com.parking.entity;

public class ParkingSlot {

    public enum Status {
        AVAILABLE,
        OCCUPIED,
        RESERVED,
        OVERDUE,
        MAINTENANCE
    }

    private Long id;
    private String slotNumber;
    private VehicleType vehicleType;
    private Status status;

    public ParkingSlot() {
    }

    public ParkingSlot(
            Long id,
            String slotNumber,
            VehicleType vehicleType,
            Status status
    ) {
        this.id = id;
        this.slotNumber = slotNumber;
        this.vehicleType = vehicleType;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}