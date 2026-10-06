package com.parking.service;

import com.parking.dto.ParkingRateResponse;
import com.parking.dto.ParkingRateUpdateRequest;
import com.parking.entity.ParkingRate;
import com.parking.entity.VehicleType;
import com.parking.repository.ParkingRateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingRateService {

    private final ParkingRateRepository parkingRateRepository;

    public ParkingRateService(ParkingRateRepository parkingRateRepository) {
        this.parkingRateRepository = parkingRateRepository;
    }

    public List<ParkingRateResponse> getAllRates() {
        return parkingRateRepository.findAll()
                .stream()
                .map(ParkingRateResponse::from)
                .toList();
    }

    public ParkingRateResponse updateRate(
            Long id,
            ParkingRateUpdateRequest request
    ) {
        ParkingRate existingRate = parkingRateRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Parking rate not found"));

        VehicleType requestedVehicleType = request.getVehicleType();

        if (requestedVehicleType == null) {
            throw new IllegalArgumentException(
                    "Vehicle type is required"
            );
        }

        if (request.getBaseDurationMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "Base duration must be positive"
            );
        }

        if (request.getExtraUnitMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "Extra unit duration must be positive"
            );
        }

        if (request.getBaseAmount() < 0) {
            throw new IllegalArgumentException(
                    "Base amount cannot be negative"
            );
        }

        if (request.getExtraAmount() < 0) {
            throw new IllegalArgumentException(
                    "Extra amount cannot be negative"
            );
        }

        parkingRateRepository.findByVehicleType(requestedVehicleType)
                .ifPresent(rate -> {
                    if (!rate.getId().equals(existingRate.getId())) {
                        throw new IllegalArgumentException(
                                "An active rate already exists for vehicle type: "
                                        + requestedVehicleType
                        );
                    }
                });

        existingRate.setVehicleType(requestedVehicleType);
        existingRate.setBaseDurationMinutes(
                request.getBaseDurationMinutes()
        );
        existingRate.setBaseAmount(
                request.getBaseAmount()
        );
        existingRate.setExtraUnitMinutes(
                request.getExtraUnitMinutes()
        );
        existingRate.setExtraAmount(
                request.getExtraAmount()
        );

        existingRate.setEffectiveFrom(LocalDateTime.now());

        parkingRateRepository.update(existingRate);

        return ParkingRateResponse.from(existingRate);
    }
}