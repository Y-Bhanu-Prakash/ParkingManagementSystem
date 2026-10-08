package com.parking;

import com.parking.entity.ParkingSession;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import com.parking.repository.ParkingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingSessionRepositoryTest {

    private InMemoryDataStore dataStore;
    private ParkingSessionRepository repository;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryDataStore();
        repository = new ParkingSessionRepository(dataStore);
    }

    @Test
    void shouldCreateAndFindSessionById() {

        ParkingSession session = createSession(
                1L,
                "AP01AB1234",
                VehicleType.CAR,
                1L,
                10L,
                ParkingSession.Status.ACTIVE
        );

        repository.save(session);

        ParkingSession saved =
                repository.findById(1L).orElseThrow();

        assertEquals(
                "AP01AB1234",
                saved.getVehicleNumber()
        );

        assertEquals(
                VehicleType.CAR,
                saved.getVehicleType()
        );
    }

    @Test
    void shouldListActiveSessions() {

        repository.save(
                createSession(
                        1L,
                        "AP01AB1234",
                        VehicleType.CAR,
                        1L,
                        10L,
                        ParkingSession.Status.ACTIVE
                )
        );

        repository.save(
                createSession(
                        2L,
                        "AP01CD5678",
                        VehicleType.BIKE,
                        2L,
                        11L,
                        ParkingSession.Status.COMPLETED
                )
        );

        List<ParkingSession> activeSessions =
                repository.findActiveSessions();

        assertEquals(1, activeSessions.size());

        assertEquals(
                "AP01AB1234",
                activeSessions.get(0).getVehicleNumber()
        );
    }

    @Test
    void shouldFindActiveSessionByVehicleNumber() {

        repository.save(
                createSession(
                        1L,
                        "AP01AB1234",
                        VehicleType.CAR,
                        1L,
                        10L,
                        ParkingSession.Status.ACTIVE
                )
        );

        assertTrue(
                repository.findActiveByVehicleNumber(
                        "AP01AB1234"
                ).isPresent()
        );
    }

    @Test
    void shouldNotFindCompletedSessionByVehicleNumber() {

        repository.save(
                createSession(
                        1L,
                        "AP01AB1234",
                        VehicleType.CAR,
                        1L,
                        10L,
                        ParkingSession.Status.COMPLETED
                )
        );

        assertTrue(
                repository.findActiveByVehicleNumber(
                        "AP01AB1234"
                ).isEmpty()
        );
    }

    @Test
    void shouldFindActiveSessionBySlotId() {

        repository.save(
                createSession(
                        1L,
                        "AP01AB1234",
                        VehicleType.CAR,
                        5L,
                        10L,
                        ParkingSession.Status.ACTIVE
                )
        );

        assertTrue(
                repository.findActiveBySlotId(5L).isPresent()
        );
    }

    @Test
    void shouldNotFindCompletedSessionBySlotId() {

        repository.save(
                createSession(
                        1L,
                        "AP01AB1234",
                        VehicleType.CAR,
                        5L,
                        10L,
                        ParkingSession.Status.COMPLETED
                )
        );

        assertTrue(
                repository.findActiveBySlotId(5L).isEmpty()
        );
    }

    @Test
    void shouldUpdateSession() {

        ParkingSession session = createSession(
                1L,
                "AP01AB1234",
                VehicleType.CAR,
                1L,
                10L,
                ParkingSession.Status.ACTIVE
        );

        repository.save(session);

        session.setStatus(
                ParkingSession.Status.COMPLETED
        );

        session.setExtraCharge(30.0);
        session.setTotalCharge(90.0);

        repository.update(session);

        ParkingSession updated =
                repository.findById(1L).orElseThrow();

        assertEquals(
                ParkingSession.Status.COMPLETED,
                updated.getStatus()
        );

        assertEquals(
                30.0,
                updated.getExtraCharge()
        );

        assertEquals(
                90.0,
                updated.getTotalCharge()
        );
    }

    @Test
    void shouldKeepSessionsInMemory() {

        ParkingSession session = createSession(
                1L,
                "AP01AB1234",
                VehicleType.CAR,
                1L,
                10L,
                ParkingSession.Status.ACTIVE
        );

        repository.save(session);

        assertEquals(
                1,
                dataStore.getParkingSessions().size()
        );

        assertTrue(
                dataStore.getParkingSessions()
                        .containsKey(1L)
        );
    }

    private ParkingSession createSession(
            Long id,
            String vehicleNumber,
            VehicleType vehicleType,
            Long slotId,
            Long attendantId,
            ParkingSession.Status status
    ) {

        LocalDateTime entryTime =
                LocalDateTime.now();

        LocalDateTime allowedUntil =
                entryTime.plusMinutes(120);

        return new ParkingSession(
                id,
                vehicleNumber,
                vehicleType,
                slotId,
                attendantId,
                entryTime,
                allowedUntil,
                120,
                status,
                60.0,
                0.0,
                60.0
        );
    }
}