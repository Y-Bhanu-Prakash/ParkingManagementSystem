package com.parking;

import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParkingSlotModelTest {

    @Test
    void shouldCreateParkingSlotWithRequiredFields() {

        LocalDateTime createdAt = LocalDateTime.now();

        ParkingSlot slot = new ParkingSlot(
                1L,
                "C-001",
                VehicleType.CAR,
                ParkingSlot.Status.AVAILABLE,
                true,
                createdAt
        );

        assertEquals(1L, slot.getId());
        assertEquals("C-001", slot.getSlotCode());
        assertEquals(VehicleType.CAR, slot.getSlotType());
        assertEquals(
                ParkingSlot.Status.AVAILABLE,
                slot.getStatus()
        );
        assertEquals(true, slot.isActive());
        assertEquals(createdAt, slot.getCreatedAt());
    }

    @Test
    void shouldSupportAllVehicleTypes() {
        assertEquals(5, VehicleType.values().length);

        assertEquals(
                VehicleType.BIKE,
                VehicleType.valueOf("BIKE")
        );

        assertEquals(
                VehicleType.CAR,
                VehicleType.valueOf("CAR")
        );

        assertEquals(
                VehicleType.SUV,
                VehicleType.valueOf("SUV")
        );

        assertEquals(
                VehicleType.TRUCK,
                VehicleType.valueOf("TRUCK")
        );

        assertEquals(
                VehicleType.OTHER,
                VehicleType.valueOf("OTHER")
        );
    }

    @Test
    void shouldSupportAllSlotStatuses() {
        assertEquals(5, ParkingSlot.Status.values().length);

        assertNotNull(
                ParkingSlot.Status.valueOf("AVAILABLE")
        );

        assertNotNull(
                ParkingSlot.Status.valueOf("OCCUPIED")
        );

        assertNotNull(
                ParkingSlot.Status.valueOf("RESERVED")
        );

        assertNotNull(
                ParkingSlot.Status.valueOf("OVERDUE")
        );

        assertNotNull(
                ParkingSlot.Status.valueOf("MAINTENANCE")
        );
    }
}