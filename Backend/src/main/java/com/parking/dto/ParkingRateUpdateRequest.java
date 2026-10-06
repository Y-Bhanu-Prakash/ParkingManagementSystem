package com.parking.dto;

import com.parking.entity.VehicleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ParkingRateUpdateRequest {

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @Positive(message = "Base duration must be positive")
    private int baseDurationMinutes;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Base amount cannot be negative"
    )
    private double baseAmount;

    @Positive(message = "Extra unit duration must be positive")
    private int extraUnitMinutes;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Extra amount cannot be negative"
    )
    private double extraAmount;

    public ParkingRateUpdateRequest() {
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
}