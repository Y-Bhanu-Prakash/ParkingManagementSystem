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

    public ParkingRate save(ParkingRate rate) {
        dataStore.getParkingRates().put(rate.getId(), rate);
        return rate;
    }

    public Optional<ParkingRate> findById(Long id) {
        Object rate = dataStore.getParkingRates().get(id);

        if (rate instanceof ParkingRate) {
            return Optional.of((ParkingRate) rate);
        }

        return Optional.empty();
    }

    public List<ParkingRate> findAll() {
        return dataStore.getParkingRates()
                .values()
                .stream()
                .filter(ParkingRate.class::isInstance)
                .map(ParkingRate.class::cast)
                .toList();
    }

    public Optional<ParkingRate> findByVehicleType(VehicleType vehicleType) {
        return findAll()
                .stream()
                .filter(rate -> rate.getVehicleType() == vehicleType)
                .findFirst();
    }

    public boolean existsByVehicleType(VehicleType vehicleType) {
        return findByVehicleType(vehicleType).isPresent();
    }

    public ParkingRate update(ParkingRate rate) {
        dataStore.getParkingRates().put(rate.getId(), rate);
        return rate;
    }
}