package com.parking;

import com.parking.config.InitialParkingDataInitializer;
import com.parking.entity.ParkingRate;
import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitialParkingDataInitializerTest {

    private InMemoryDataStore dataStore;
    private InitialParkingDataInitializer initializer;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryDataStore();
        initializer = new InitialParkingDataInitializer(dataStore);
    }

    @Test
    void shouldCreateInitialParkingSlots() {

        initializer.run();

        Map<Long, Object> slots = dataStore.getParkingSlots();

        assertEquals(10, slots.size());

        assertEquals(
                3,
                slots.values().stream()
                        .filter(ParkingSlot.class::isInstance)
                        .map(ParkingSlot.class::cast)
                        .filter(slot -> slot.getSlotType() == VehicleType.CAR)
                        .count()
        );

        assertEquals(
                3,
                slots.values().stream()
                        .filter(ParkingSlot.class::isInstance)
                        .map(ParkingSlot.class::cast)
                        .filter(slot -> slot.getSlotType() == VehicleType.BIKE)
                        .count()
        );

        assertEquals(
                2,
                slots.values().stream()
                        .filter(ParkingSlot.class::isInstance)
                        .map(ParkingSlot.class::cast)
                        .filter(slot -> slot.getSlotType() == VehicleType.SUV)
                        .count()
        );

        assertEquals(
                2,
                slots.values().stream()
                        .filter(ParkingSlot.class::isInstance)
                        .map(ParkingSlot.class::cast)
                        .filter(slot -> slot.getSlotType() == VehicleType.TRUCK)
                        .count()
        );
    }

    @Test
    void allInitialSlotsShouldBeAvailable() {

        initializer.run();

        boolean allAvailable = dataStore.getParkingSlots()
                .values()
                .stream()
                .filter(ParkingSlot.class::isInstance)
                .map(ParkingSlot.class::cast)
                .allMatch(slot ->
                        slot.getStatus() == ParkingSlot.Status.AVAILABLE
                );

        assertTrue(allAvailable);
    }

    @Test
    void shouldCreateInitialParkingRates() {

        initializer.run();

        Map<Long, Object> rates = dataStore.getParkingRates();

        assertEquals(5, rates.size());

        assertTrue(
                rates.values()
                        .stream()
                        .filter(ParkingRate.class::isInstance)
                        .map(ParkingRate.class::cast)
                        .anyMatch(rate ->
                                rate.getVehicleType() == VehicleType.CAR
                                        && rate.getBaseDurationMinutes() == 120
                                        && rate.getBaseAmount() == 60.0
                                        && rate.getExtraUnitMinutes() == 60
                                        && rate.getExtraAmount() == 30.0
                        )
        );

        assertTrue(
                rates.values()
                        .stream()
                        .filter(ParkingRate.class::isInstance)
                        .map(ParkingRate.class::cast)
                        .anyMatch(rate ->
                                rate.getVehicleType() == VehicleType.BIKE
                                        && rate.getBaseDurationMinutes() == 120
                                        && rate.getBaseAmount() == 30.0
                                        && rate.getExtraUnitMinutes() == 60
                                        && rate.getExtraAmount() == 10.0
                        )
        );
    }

    @Test
    void shouldNotCreateDuplicateData() {

        initializer.run();

        int slotCount = dataStore.getParkingSlots().size();
        int rateCount = dataStore.getParkingRates().size();

        initializer.run();

        assertEquals(
                slotCount,
                dataStore.getParkingSlots().size()
        );

        assertEquals(
                rateCount,
                dataStore.getParkingRates().size()
        );
    }
}