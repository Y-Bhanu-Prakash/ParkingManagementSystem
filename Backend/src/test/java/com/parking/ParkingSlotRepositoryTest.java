package com.parking;

import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import com.parking.repository.ParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingSlotRepositoryTest {

    private InMemoryDataStore dataStore;
    private ParkingSlotRepository repository;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryDataStore();
        repository = new ParkingSlotRepository(dataStore);
    }

    @Test
    void shouldAddAndFindSlotById() {
        ParkingSlot slot = createSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        repository.save(slot);

        assertTrue(repository.findById(1L).isPresent());
        assertEquals(
                "C-001",
                repository.findById(1L).get().getSlotCode()
        );
    }

    @Test
    void shouldFindSlotBySlotCode() {
        ParkingSlot slot = createSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        repository.save(slot);

        assertTrue(
                repository.findBySlotCode("C-001").isPresent()
        );

        assertEquals(
                1L,
                repository.findBySlotCode("C-001")
                        .get()
                        .getId()
        );
    }

    @Test
    void slotCodeSearchShouldBeCaseInsensitive() {
        ParkingSlot slot = createSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        repository.save(slot);

        assertTrue(
                repository.findBySlotCode("c-001").isPresent()
        );
    }

    @Test
    void shouldListAllSlots() {
        repository.save(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        repository.save(
                createSlot(2L, "B-001", VehicleType.BIKE)
        );

        List<ParkingSlot> slots = repository.findAll();

        assertEquals(2, slots.size());
    }

    @Test
    void shouldUpdateSlot() {
        ParkingSlot slot = createSlot(
                1L,
                "C-001",
                VehicleType.CAR
        );

        repository.save(slot);

        slot.setStatus(ParkingSlot.Status.OCCUPIED);
        slot.setActive(false);

        repository.update(slot);

        ParkingSlot updated = repository.findById(1L).orElseThrow();

        assertEquals(
                ParkingSlot.Status.OCCUPIED,
                updated.getStatus()
        );

        assertFalse(updated.isActive());
    }

    @Test
    void shouldDetectDuplicateSlotCode() {
        repository.save(
                createSlot(1L, "C-001", VehicleType.CAR)
        );

        assertTrue(
                repository.existsBySlotCode("C-001")
        );

        assertTrue(
                repository.existsBySlotCode("c-001")
        );
    }

    @Test
    void shouldReturnFalseForUnusedSlotCode() {
        assertFalse(
                repository.existsBySlotCode("C-999")
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