package com.parking;

import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import com.parking.repository.ParkingSlotRepository;
import com.parking.service.ParkingSlotService;
import com.parking.service.ParkingSlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingSlotServiceTest {

    private ParkingSlotService service;

    @BeforeEach
    void setUp() {
        InMemoryDataStore dataStore = new InMemoryDataStore();
        ParkingSlotRepository repository =
                new ParkingSlotRepository(dataStore);

        service = new ParkingSlotServiceImpl(repository);
    }

    @Test
    void shouldCreateSlot() {
        ParkingSlot slot = createSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        ParkingSlot created = service.createSlot(slot);

        assertEquals("C-001", created.getSlotCode());
        assertEquals(
                VehicleType.CAR,
                created.getSlotType()
        );
        assertEquals(
                ParkingSlot.Status.AVAILABLE,
                created.getStatus()
        );
        assertTrue(created.isActive());
    }

    @Test
    void shouldRejectDuplicateSlotCode() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createSlot(
                        createSlot(2L, "C-001", VehicleType.CAR)
                )
        );
    }

    @Test
    void shouldRejectMissingSlotCode() {
        ParkingSlot slot = createSlot(
                1L,
                null,
                VehicleType.CAR
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createSlot(slot)
        );
    }

    @Test
    void shouldRejectMissingSlotType() {
        ParkingSlot slot = createSlot(
                1L,
                "C-001",
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createSlot(slot)
        );
    }

    @Test
    void shouldGetAllSlots() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        service.createSlot(
                createSlot(2L, "B-001", VehicleType.BIKE)
        );

        List<ParkingSlot> slots = service.getAllSlots();

        assertEquals(2, slots.size());
    }

    @Test
    void shouldGetSlotById() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        ParkingSlot slot = service.getSlotById(1L);

        assertEquals("C-001", slot.getSlotCode());
    }

    @Test
    void shouldRejectInvalidSlotId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getSlotById(999L)
        );
    }

    @Test
    void shouldUpdateSlot() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        ParkingSlot update = createSlot(
                1L,
                "C-002",
                VehicleType.BIKE
        );

        ParkingSlot updated =
                service.updateSlot(1L, update);

        assertEquals(
                "C-002",
                updated.getSlotCode()
        );

        assertEquals(
                VehicleType.BIKE,
                updated.getSlotType()
        );
    }

    @Test
    void shouldRejectDuplicateCodeDuringUpdate() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        service.createSlot(
                createSlot(2L, "C-002", VehicleType.CAR)
        );

        ParkingSlot update = createSlot(
                2L,
                "C-001",
                VehicleType.BIKE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateSlot(2L, update)
        );
    }

    @Test
    void shouldChangeSlotStatus() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        ParkingSlot updated =
                service.changeStatus(
                        1L,
                        ParkingSlot.Status.RESERVED
                );

        assertEquals(
                ParkingSlot.Status.RESERVED,
                updated.getStatus()
        );
    }

    @Test
    void shouldRejectChangingOccupiedSlotToMaintenance() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        service.changeStatus(
                1L,
                ParkingSlot.Status.OCCUPIED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.changeStatus(
                        1L,
                        ParkingSlot.Status.MAINTENANCE
                )
        );
    }

    @Test
    void shouldDeactivateActiveSlot() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        ParkingSlot slot =
                service.deactivateSlot(1L);

        assertEquals(false, slot.isActive());
    }

    @Test
    void shouldRejectDeactivatingOccupiedSlot() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        service.changeStatus(
                1L,
                ParkingSlot.Status.OCCUPIED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.deactivateSlot(1L)
        );
    }

    @Test
    void shouldActivateInactiveSlot() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        service.deactivateSlot(1L);

        ParkingSlot slot =
                service.activateSlot(1L);

        assertTrue(slot.isActive());
    }

    @Test
    void shouldRejectInvalidStatus() {
        service.createSlot(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.changeStatus(
                        1L,
                        ParkingSlot.Status.AVAILABLE
                )
        );
    }

    @Test
    void shouldRejectStatusChangeForInvalidSlotId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeStatus(
                        999L,
                        ParkingSlot.Status.OCCUPIED
                )
        );
    }

    private ParkingSlot createSlot(
            Long id,
            String slotCode,
            VehicleType vehicleType
    ) {
        return new ParkingSlot(
                id,
                slotCode,
                vehicleType,
                ParkingSlot.Status.AVAILABLE,
                true,
                LocalDateTime.now()
        );
    }
}