package com.parking.service;

import com.parking.entity.ParkingSession;
import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.ParkingSessionRepository;
import com.parking.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ParkingSlotAssignmentService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSessionRepository parkingSessionRepository;

    public ParkingSlotAssignmentService(
            ParkingSlotRepository parkingSlotRepository,
            ParkingSessionRepository parkingSessionRepository
    ) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
    }

    public ParkingSession assignSlot(
            String vehicleNumber,
            VehicleType vehicleType,
            Long attendantId,
            int durationMinutes,
            double baseCharge
    ) {

        if (vehicleNumber == null
                || vehicleNumber.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Vehicle number is required"
            );
        }

        if (vehicleType == null) {

            throw new IllegalArgumentException(
                    "Vehicle type is required"
            );
        }

        if (durationMinutes <= 0) {

            throw new IllegalArgumentException(
                    "Parking duration must be greater than zero"
            );
        }

        String normalizedVehicleNumber =
                vehicleNumber
                        .trim()
                        .replaceAll("\\s+", "")
                        .toUpperCase();

        if (parkingSessionRepository
                .findActiveByVehicleNumber(
                        normalizedVehicleNumber
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "An active parking session already exists "
                            + "for vehicle: "
                            + normalizedVehicleNumber
            );
        }

        /*
         * Find the slot before changing any state.
         * If no compatible slot exists, nothing is modified.
         */
        ParkingSlot assignedSlot =
                parkingSlotRepository.findAll()
                        .stream()
                        .filter(slot ->
                                slot.isActive()
                                        && slot.getStatus()
                                        == ParkingSlot.Status.AVAILABLE
                        )
                        .filter(slot ->
                                slot.getSlotType()
                                        == vehicleType
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No available compatible "
                                                + "parking slot exists "
                                                + "for vehicle type: "
                                                + vehicleType
                                )
                        );

        LocalDateTime entryTime =
                LocalDateTime.now();

        LocalDateTime allowedUntil =
                entryTime.plusMinutes(durationMinutes);

        /*
         * Create the session first.
         */
        ParkingSession session =
                new ParkingSession(
                        System.currentTimeMillis(),
                        normalizedVehicleNumber,
                        vehicleType,
                        assignedSlot.getId(),
                        attendantId,
                        entryTime,
                        allowedUntil,
                        durationMinutes,
                        ParkingSession.Status.ACTIVE,
                        baseCharge,
                        0.0,
                        baseCharge
                );

        /*
         * Save the session and occupy the slot.
         */
        parkingSessionRepository.save(session);

        assignedSlot.setStatus(
                ParkingSlot.Status.OCCUPIED
        );

        parkingSlotRepository.update(
                assignedSlot
        );

        return session;
    }
}