package com.parking.entity;

import java.time.LocalDateTime;

public class ParkingRate {

    private Long id;
    private VehicleType vehicleType;

    private int baseDurationMinutes;
    private double baseAmount;

    private int extraUnitMinutes;
    private double extraUnitAmount;

    private boolean active;
    private LocalDateTime effectiveFrom;

    public ParkingRate() {
    }

    public ParkingRate(
            Long id,
            VehicleType vehicleType,
            int baseDurationMinutes,
            double baseAmount,
            int extraUnitMinutes,
            double extraUnitAmount,
            boolean active,
            LocalDateTime effectiveFrom
    ) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.baseDurationMinutes = baseDurationMinutes;
        this.baseAmount = baseAmount;
        this.extraUnitMinutes = extraUnitMinutes;
        this.extraUnitAmount = extraUnitAmount;
        this.active = active;
        this.effectiveFrom = effectiveFrom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getBaseDurationMinutes() {
        return baseDurationMinutes;
    }

    public void setBaseDurationMinutes(int baseDurationMinutes) {
        this.baseDurationMinutes = baseDurationMinutes;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public int getExtraUnitMinutes() {
        return extraUnitMinutes;
    }

    public void setExtraUnitMinutes(int extraUnitMinutes) {
        this.extraUnitMinutes = extraUnitMinutes;
    }

    public double getExtraUnitAmount() {
        return extraUnitAmount;
    }

    public void setExtraUnitAmount(double extraUnitAmount) {
        this.extraUnitAmount = extraUnitAmount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}