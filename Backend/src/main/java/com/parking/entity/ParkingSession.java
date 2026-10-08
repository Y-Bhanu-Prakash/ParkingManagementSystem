package com.parking.entity;

import java.time.LocalDateTime;

public class ParkingSession {

    public enum Status {
        ACTIVE,
        COMPLETED
    }

    private Long id;

    private String vehicleNumber;

    private VehicleType vehicleType;

    private Long slotId;

    private Long attendantId;

    private LocalDateTime entryTime;

    private LocalDateTime allowedUntil;

    private int expectedDurationMinutes;

    private Status status;

    private double baseCharge;

    private double extraCharge;

    private double totalCharge;

    public ParkingSession() {
    }

    public ParkingSession(
            Long id,
            String vehicleNumber,
            VehicleType vehicleType,
            Long slotId,
            Long attendantId,
            LocalDateTime entryTime,
            LocalDateTime allowedUntil,
            int expectedDurationMinutes,
            Status status,
            double baseCharge,
            double extraCharge,
            double totalCharge
    ) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.slotId = slotId;
        this.attendantId = attendantId;
        this.entryTime = entryTime;
        this.allowedUntil = allowedUntil;
        this.expectedDurationMinutes = expectedDurationMinutes;
        this.status = status;
        this.baseCharge = baseCharge;
        this.extraCharge = extraCharge;
        this.totalCharge = totalCharge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getAttendantId() {
        return attendantId;
    }

    public void setAttendantId(Long attendantId) {
        this.attendantId = attendantId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getAllowedUntil() {
        return allowedUntil;
    }

    public void setAllowedUntil(LocalDateTime allowedUntil) {
        this.allowedUntil = allowedUntil;
    }

    public int getExpectedDurationMinutes() {
        return expectedDurationMinutes;
    }

    public void setExpectedDurationMinutes(int expectedDurationMinutes) {
        this.expectedDurationMinutes = expectedDurationMinutes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getBaseCharge() {
        return baseCharge;
    }

    public void setBaseCharge(double baseCharge) {
        this.baseCharge = baseCharge;
    }

    public double getExtraCharge() {
        return extraCharge;
    }

    public void setExtraCharge(double extraCharge) {
        this.extraCharge = extraCharge;
    }

    public double getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(double totalCharge) {
        this.totalCharge = totalCharge;
    }
}