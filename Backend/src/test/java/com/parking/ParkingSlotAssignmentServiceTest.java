package com.parking;

import com.parking.entity.ParkingSession;
import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import com.parking.repository.ParkingPassRepository;
import com.parking.repository.ParkingSessionRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.service.ParkingPassService;
import com.parking.service.ParkingSlotAssignmentService;
import com.parking.service.VehicleEntryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingSlotAssignmentServiceTest {

    private InMemoryDataStore dataStore;
    private ParkingSlotRepository parkingSlotRepository;
    private ParkingSessionRepository parkingSessionRepository;
    private ParkingPassRepository parkingPassRepository;
    private ParkingPassService parkingPassService;
    private ParkingSlotAssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryDataStore();

        parkingSlotRepository =
                new ParkingSlotRepository(dataStore);

        parkingSessionRepository =
                new ParkingSessionRepository(dataStore);

        parkingPassRepository =
                new ParkingPassRepository(dataStore);

        parkingPassService = new ParkingPassService(
                parkingPassRepository,
                new BCryptPasswordEncoder()
        );

        assignmentService = new ParkingSlotAssignmentService(
                parkingSlotRepository,
                parkingSessionRepository,
                parkingPassService
        );
    }

    @Test
    void shouldAssignCompatibleAvailableSlot() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        VehicleEntryResponse response =
                assignmentService.assignSlot(
                        "AP01AB1234",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                );

        ParkingSession session = response.session();

        assertEquals("AP01AB1234", session.getVehicleNumber());
        assertEquals(VehicleType.CAR, session.getVehicleType());
        assertEquals(1L, session.getSlotId());
        assertEquals(ParkingSession.Status.ACTIVE, session.getStatus());

        assertFalse(response.passCode().isBlank());
        assertTrue(response.password().matches("\\d{4}"));
        assertEquals(session.getId(), parkingPassRepository
                .findByPassCode(response.passCode())
                .orElseThrow()
                .getSessionId());

        assertEquals(
                ParkingSlot.Status.OCCUPIED,
                parkingSlotRepository.findById(1L)
                        .orElseThrow()
                        .getStatus()
        );
    }

    @Test
    void shouldIgnoreOccupiedSlots() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.OCCUPIED,
                true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        "AP01AB1234",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                )
        );
    }

    @Test
    void shouldIgnoreIncompatibleSlots() {
        addSlot(
                1L,
                "B-001",
                VehicleType.BIKE,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        "AP01AB1234",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                )
        );

        assertEquals(
                ParkingSlot.Status.AVAILABLE,
                parkingSlotRepository.findById(1L)
                        .orElseThrow()
                        .getStatus()
        );
    }

    @Test
    void shouldIgnoreInactiveCompatibleSlots() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                false
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        "AP01AB1234",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                )
        );
    }

    @Test
    void shouldNotChangeAnySlotWhenAssignmentFails() {
        addSlot(
                1L,
                "B-001",
                VehicleType.BIKE,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        addSlot(
                2L,
                "SUV-001",
                VehicleType.SUV,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        "AP01AB1234",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                )
        );

        assertEquals(
                ParkingSlot.Status.AVAILABLE,
                parkingSlotRepository.findById(1L)
                        .orElseThrow()
                        .getStatus()
        );

        assertEquals(
                ParkingSlot.Status.AVAILABLE,
                parkingSlotRepository.findById(2L)
                        .orElseThrow()
                        .getStatus()
        );

        assertEquals(
                0,
                parkingSessionRepository.findAll().size()
        );
    }

    @Test
    void shouldNotAssignSameSlotToSecondActiveVehicle() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        VehicleEntryResponse firstResponse =
                assignmentService.assignSlot(
                        "AP01AB1234",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                );

        ParkingSession firstSession = firstResponse.session();

        assertEquals(1L, firstSession.getSlotId());

        assertEquals(
                ParkingSlot.Status.OCCUPIED,
                parkingSlotRepository.findById(1L)
                        .orElseThrow()
                        .getStatus()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        "AP02CD5678",
                        VehicleType.CAR,
                        11L,
                        120,
                        60.0
                )
        );

        assertEquals(
                1,
                parkingSessionRepository.findActiveSessions().size()
        );
    }

    @Test
    void shouldRejectDuplicateActiveVehicle() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true
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
                () -> assignmentService.assignSlot(
                        "ap 01 ab 1234",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                )
        );

        assertEquals(
                ParkingSlot.Status.AVAILABLE,
                parkingSlotRepository.findById(1L)
                        .orElseThrow()
                        .getStatus()
        );
    }

    @Test
    void shouldRejectMissingVehicleNumber() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        null,
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                )
        );
    }

    @Test
    void shouldRejectMissingVehicleType() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        "AP01AB1234",
                        null,
                        10L,
                        120,
                        60.0
                )
        );
    }

    @Test
    void shouldRejectInvalidDuration() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> assignmentService.assignSlot(
                        "AP01AB1234",
                        VehicleType.CAR,
                        10L,
                        0,
                        60.0
                )
        );

        assertEquals(
                ParkingSlot.Status.AVAILABLE,
                parkingSlotRepository.findById(1L)
                        .orElseThrow()
                        .getStatus()
        );
    }

    @Test
    void shouldCreateActiveParkingSession() {
        addSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true
        );

        VehicleEntryResponse response =
                assignmentService.assignSlot(
                        " ap 01 ab 1234 ",
                        VehicleType.CAR,
                        10L,
                        120,
                        60.0
                );

        ParkingSession session = response.session();

        assertEquals("AP01AB1234", session.getVehicleNumber());
        assertEquals(ParkingSession.Status.ACTIVE, session.getStatus());
        assertEquals(120, session.getExpectedDurationMinutes());
        assertEquals(60.0, session.getBaseCharge());
        assertEquals(60.0, session.getTotalCharge());

        assertFalse(response.passCode().isBlank());
        assertTrue(response.password().matches("\\d{4}"));

        assertEquals(
                1,
                parkingSessionRepository.findActiveSessions().size()
        );
    }

    private void addSlot(
            Long id,
            String slotCode,
            VehicleType vehicleType,
            ParkingSlot.Status status,
            boolean active
    ) {
        ParkingSlot slot = new ParkingSlot(
                id,
                slotCode,
                vehicleType,
                status,
                active,
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
        LocalDateTime entryTime = LocalDateTime.now();

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
