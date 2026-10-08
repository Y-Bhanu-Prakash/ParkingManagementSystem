package com.parking.repository;

import com.parking.entity.ParkingRate;
import com.parking.entity.VehicleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ParkingRateRepository {

    private final InMemoryDataStore dataStore;

    public ParkingRateRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Add or store a parking rate in memory.
     *
     * Only one active rate is allowed for each vehicle type.
     */
    public ParkingRate save(ParkingRate rate) {

        if (rate.isActive()) {

            Optional<ParkingRate> existingRate =
                    findByVehicleType(rate.getVehicleType());

            if (existingRate.isPresent()
                    && !existingRate.get().getId().equals(rate.getId())) {

                throw new IllegalArgumentException(
                        "An active rate already exists for vehicle type: "
                                + rate.getVehicleType()
                );
            }
        }

        dataStore.getParkingRates().put(
                rate.getId(),
                rate
        );

        return rate;
    }

    /**
     * Find a parking rate by its ID.
     */
    public Optional<ParkingRate> findById(Long id) {

        Object rate = dataStore.getParkingRates().get(id);

        if (rate instanceof ParkingRate) {
            return Optional.of((ParkingRate) rate);
        }

        return Optional.empty();
    }

    /**
     * Find the active parking rate for a vehicle type.
     */
    public Optional<ParkingRate> findByVehicleType(
            VehicleType vehicleType
    ) {

        return findAll()
                .stream()
                .filter(ParkingRate::isActive)
                .filter(rate ->
                        rate.getVehicleType() == vehicleType
                )
                .findFirst();
    }

    /**
     * List all parking rates.
     */
    public List<ParkingRate> findAll() {

        return dataStore.getParkingRates()
                .values()
                .stream()
                .filter(ParkingRate.class::isInstance)
                .map(ParkingRate.class::cast)
                .toList();
    }

    /**
     * Update an existing parking rate.
     *
     * Only one active rate is allowed for each vehicle type.
     */
    public ParkingRate update(ParkingRate rate) {

        if (rate.isActive()) {

            Optional<ParkingRate> existingRate =
                    findByVehicleType(rate.getVehicleType());

            if (existingRate.isPresent()
                    && !existingRate.get().getId().equals(rate.getId())) {

                throw new IllegalArgumentException(
                        "An active rate already exists for vehicle type: "
                                + rate.getVehicleType()
                );
            }
        }

        dataStore.getParkingRates().put(
                rate.getId(),
                rate
        );

        return rate;
    }

    /**
     * Check whether an active rate already exists
     * for the specified vehicle type.
     */
    public boolean existsActiveByVehicleType(
            VehicleType vehicleType
    ) {

        return findByVehicleType(vehicleType).isPresent();
    }
}