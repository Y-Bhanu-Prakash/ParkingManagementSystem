package com.parking.service;

import com.parking.entity.ParkingSession;
import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.ParkingSessionRepository;
import com.parking.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleEntryValidationService {

    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public VehicleEntryValidationService(
            ParkingSessionRepository parkingSessionRepository,
            ParkingSlotRepository parkingSlotRepository
    ) {
        this.parkingSessionRepository = parkingSessionRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    /**
     * Validate vehicle entry information before
     * a parking session is created.
     *
     * @param vehicleNumber vehicle registration number
     * @param vehicleType vehicle type
     * @param durationMinutes requested parking duration
     * @return normalized vehicle number
     */
    public String validateEntry(
            String vehicleNumber,
            VehicleType vehicleType,
            int durationMinutes
    ) {

        String normalizedVehicleNumber =
                normalizeVehicleNumber(vehicleNumber);

        validateVehicleType(vehicleType);
        validateDuration(durationMinutes);
        validateDuplicateActiveSession(
                normalizedVehicleNumber
        );
        validateCompatibleAvailableSlot(
                vehicleType
        );

        return normalizedVehicleNumber;
    }

    /**
     * Normalize vehicle number consistently.
     *
     * Removes leading/trailing spaces,
     * removes spaces inside the number,
     * and converts it to uppercase.
     */
    public String normalizeVehicleNumber(
            String vehicleNumber
    ) {

        if (vehicleNumber == null
                || vehicleNumber.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Vehicle number is required"
            );
        }

        return vehicleNumber
                .trim()
                .replaceAll("\\s+", "")
                .toUpperCase();
    }

    /**
     * Validate vehicle type.
     */
    private void validateVehicleType(
            VehicleType vehicleType
    ) {

        if (vehicleType == null) {
            throw new IllegalArgumentException(
                    "Vehicle type is required"
            );
        }
    }

    /**
     * Validate parking duration.
     */
    private void validateDuration(
            int durationMinutes
    ) {

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Parking duration must be greater than zero"
            );
        }
    }

    /**
     * Prevent multiple active sessions
     * for the same vehicle.
     */
    private void validateDuplicateActiveSession(
            String vehicleNumber
    ) {

        if (parkingSessionRepository
                .findActiveByVehicleNumber(vehicleNumber)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "An active parking session already exists "
                            + "for vehicle: "
                            + vehicleNumber
            );
        }
    }

    /**
     * Validate that at least one compatible
     * AVAILABLE slot exists.
     */
    private void validateCompatibleAvailableSlot(
            VehicleType vehicleType
    ) {

        boolean compatibleSlotExists =
                parkingSlotRepository.findAll()
                        .stream()
                        .anyMatch(slot ->
                                slot.isActive()
                                        && slot.getStatus()
                                        == ParkingSlot.Status.AVAILABLE
                                        && slot.getSlotType()
                                        == vehicleType
                        );

        if (!compatibleSlotExists) {
            throw new IllegalArgumentException(
                    "No available parking slot exists "
                            + "for vehicle type: "
                            + vehicleType
            );
        }
    }
}