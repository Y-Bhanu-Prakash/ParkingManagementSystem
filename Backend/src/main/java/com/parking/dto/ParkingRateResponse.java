package com.parking.dto;

import com.parking.entity.ParkingRate;
import com.parking.entity.VehicleType;

import java.time.LocalDateTime;

public class ParkingRateResponse {

    private Long id;
    private VehicleType vehicleType;
    private int baseDurationMinutes;
    private double baseAmount;
    private int extraUnitMinutes;
    private double extraAmount;
    private LocalDateTime effectiveFrom;

    public ParkingRateResponse() {
    }

    public ParkingRateResponse(
            Long id,
            VehicleType vehicleType,
            int baseDurationMinutes,
            double baseAmount,
            int extraUnitMinutes,
            double extraAmount,
            LocalDateTime effectiveFrom
    ) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.baseDurationMinutes = baseDurationMinutes;
        this.baseAmount = baseAmount;
        this.extraUnitMinutes = extraUnitMinutes;
        this.extraAmount = extraAmount;
        this.effectiveFrom = effectiveFrom;
    }

    public static ParkingRateResponse from(ParkingRate rate) {
        return new ParkingRateResponse(
                rate.getId(),
                rate.getVehicleType(),
                rate.getBaseDurationMinutes(),
                rate.getBaseAmount(),
                rate.getExtraUnitMinutes(),
                rate.getExtraAmount(),
                rate.getEffectiveFrom()
        );
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

    public double getExtraAmount() {
        return extraAmount;
    }

    public void setExtraAmount(double extraAmount) {
        this.extraAmount = extraAmount;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}