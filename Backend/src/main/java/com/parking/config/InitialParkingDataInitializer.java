package com.parking.config;

import com.parking.entity.ParkingRate;
import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialParkingDataInitializer implements CommandLineRunner {

    private final InMemoryDataStore dataStore;

    public InitialParkingDataInitializer(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void run(String... args) {

        initializeParkingSlots();
        initializeParkingRates();
    }

    private void initializeParkingSlots() {

        if (!dataStore.getParkingSlots().isEmpty()) {
            return;
        }

        long id = 1L;

        id = addSlot(id, "C-001", VehicleType.CAR);
        id = addSlot(id, "C-002", VehicleType.CAR);
        id = addSlot(id, "C-003", VehicleType.CAR);

        id = addSlot(id, "B-001", VehicleType.BIKE);
        id = addSlot(id, "B-002", VehicleType.BIKE);
        id = addSlot(id, "B-003", VehicleType.BIKE);

        id = addSlot(id, "S-001", VehicleType.SUV);
        id = addSlot(id, "S-002", VehicleType.SUV);

        id = addSlot(id, "T-001", VehicleType.TRUCK);
        id = addSlot(id, "T-002", VehicleType.TRUCK);
    }

    private long addSlot(
            long id,
            String slotNumber,
            VehicleType vehicleType
    ) {

        ParkingSlot slot = new ParkingSlot(
                id,
                slotNumber,
                vehicleType,
                ParkingSlot.Status.AVAILABLE
        );

        dataStore.getParkingSlots().put(id, slot);

        return id + 1;
    }

    private void initializeParkingRates() {

        if (!dataStore.getParkingRates().isEmpty()) {
            return;
        }

        long id = 1L;

        addRate(
                id++,
                VehicleType.CAR,
                120,
                60.0,
                60,
                30.0
        );

        addRate(
                id++,
                VehicleType.BIKE,
                120,
                30.0,
                60,
                10.0
        );

        addRate(
                id++,
                VehicleType.SUV,
                120,
                80.0,
                60,
                40.0
        );

        addRate(
                id++,
                VehicleType.TRUCK,
                120,
                100.0,
                60,
                50.0
        );

        addRate(
                id,
                VehicleType.OTHER,
                120,
                50.0,
                60,
                25.0
        );
    }

    private void addRate(
            long id,
            VehicleType vehicleType,
            int baseDurationMinutes,
            double baseAmount,
            int extraUnitMinutes,
            double extraAmount
    ) {

        ParkingRate rate = new ParkingRate(
                id,
                vehicleType,
                baseDurationMinutes,
                baseAmount,
                extraUnitMinutes,
                extraAmount
        );

        dataStore.getParkingRates().put(id, rate);
    }
}