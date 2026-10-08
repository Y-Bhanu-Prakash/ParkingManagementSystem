package com.parking;

import com.parking.entity.ParkingSession;
import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import com.parking.repository.ParkingSessionRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.service.VehicleEntryValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VehicleEntryValidationServiceTest {

    private InMemoryDataStore dataStore;
    private ParkingSlotRepository parkingSlotRepository;
    private ParkingSessionRepository parkingSessionRepository;
    private VehicleEntryValidationService validationService;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryDataStore();

        parkingSlotRepository =
                new ParkingSlotRepository(dataStore);

        parkingSessionRepository =
                new ParkingSessionRepository(dataStore);

        validationService =
                new VehicleEntryValidationService(
                        parkingSessionRepository,
                        parkingSlotRepository
                );
    }

    @Test
    void shouldNormalizeVehicleNumber() {

        addAvailableSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        String normalized =
                validationService.validateEntry(
                        " ap 01 ab 1234 ",
                        VehicleType.CAR,
                        120
                );

        assertEquals(
                "AP01AB1234",
                normalized
        );
    }

    @Test
    void shouldRejectMissingVehicleNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        null,
                        VehicleType.CAR,
                        120
                )
        );
    }

    @Test
    void shouldRejectBlankVehicleNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "   ",
                        VehicleType.CAR,
                        120
                )
        );
    }

    @Test
    void shouldRejectMissingVehicleType() {

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "AP01AB1234",
                        null,
                        120
                )
        );
    }

    @Test
    void shouldRejectZeroDuration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "AP01AB1234",
                        VehicleType.CAR,
                        0
                )
        );
    }

    @Test
    void shouldRejectNegativeDuration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "AP01AB1234",
                        VehicleType.CAR,
                        -30
                )
        );
    }

    @Test
    void shouldRejectDuplicateActiveVehicleSession() {

        addAvailableSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        parkingSessionRepository.save(
                createActiveSession(
                        1L,
                        "AP01AB1234",
                        VehicleType.CAR,
                        1L
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "ap 01 ab 1234",
                        VehicleType.CAR,
                        120
                )
        );
    }

    @Test
    void shouldRejectWhenNoCompatibleSlotExists() {

        addAvailableSlot(
                1L,
                "B-001",
                VehicleType.BIKE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "AP01AB1234",
                        VehicleType.CAR,
                        120
                )
        );
    }

    @Test
    void shouldRejectOccupiedCompatibleSlot() {

        ParkingSlot slot = new ParkingSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.OCCUPIED,
                true,
                LocalDateTime.now()
        );

        parkingSlotRepository.save(slot);

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "AP01AB1234",
                        VehicleType.CAR,
                        120
                )
        );
    }

    @Test
    void shouldRejectInactiveCompatibleSlot() {

        ParkingSlot slot = new ParkingSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                false,
                LocalDateTime.now()
        );

        parkingSlotRepository.save(slot);

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "AP01AB1234",
                        VehicleType.CAR,
                        120
                )
        );
    }

    @Test
    void shouldRejectIncompatibleAvailableSlot() {

        addAvailableSlot(
                1L,
                "B-001",
                VehicleType.BIKE
        );

        addAvailableSlot(
                2L,
                "S-001",
                VehicleType.SUV
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateEntry(
                        "AP01AB1234",
                        VehicleType.CAR,
                        120
                )
        );
    }

    @Test
    void shouldAcceptCompatibleAvailableSlot() {

        addAvailableSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        String normalized =
                validationService.validateEntry(
                        " ap 01 ab 1234 ",
                        VehicleType.CAR,
                        120
                );

        assertEquals(
                "AP01AB1234",
                normalized
        );
    }

    private void addAvailableSlot(
            Long id,
            String slotCode,
            VehicleType vehicleType
    ) {

        ParkingSlot slot = new ParkingSlot(
                id,
                slotCode,
                vehicleType,
                ParkingSlot.Status.AVAILABLE,
                true,
                LocalDateTime.now()
        );

        parkingSlotRepository.save(slot);
    }

    private ParkingSession createActiveSession(
            Long id,
            String vehicleNumber,
            VehicleType vehicleType,
            Long slotId
    ) {

        LocalDateTime entryTime =
                LocalDateTime.now();

        return new ParkingSession(
                id,
                vehicleNumber,
                vehicleType,
                slotId,
                10L,
                entryTime,
                entryTime.plusMinutes(120),
                120,
                ParkingSession.Status.ACTIVE,
                60.0,
                0.0,
                60.0
        );
    }
}