package com.parking;

import com.parking.entity.ParkingRate;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import com.parking.repository.ParkingRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingRateRepositoryTest {

    private InMemoryDataStore dataStore;
    private ParkingRateRepository repository;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryDataStore();
        repository = new ParkingRateRepository(dataStore);
    }

    @Test
    void shouldAddAndFindRateById() {

        ParkingRate rate = createRate(
                1L,
                VehicleType.CAR,
                true
        );

        repository.save(rate);

        assertTrue(repository.findById(1L).isPresent());

        assertEquals(
                VehicleType.CAR,
                repository.findById(1L)
                        .get()
                        .getVehicleType()
        );
    }

    @Test
    void shouldFindActiveRateByVehicleType() {

        ParkingRate rate = createRate(
                1L,
                VehicleType.CAR,
                true
        );

        repository.save(rate);

        assertTrue(
                repository.findByVehicleType(
                        VehicleType.CAR
                ).isPresent()
        );
    }

    @Test
    void shouldNotReturnInactiveRateByVehicleType() {

        ParkingRate rate = createRate(
                1L,
                VehicleType.CAR,
                false
        );

        repository.save(rate);

        assertTrue(
                repository.findByVehicleType(
                        VehicleType.CAR
                ).isEmpty()
        );
    }

    @Test
    void shouldListAllRates() {

        repository.save(
                createRate(
                        1L,
                        VehicleType.CAR,
                        true
                )
        );

        repository.save(
                createRate(
                        2L,
                        VehicleType.BIKE,
                        true
                )
        );

        List<ParkingRate> rates = repository.findAll();

        assertEquals(2, rates.size());
    }

    @Test
    void shouldUpdateRate() {

        ParkingRate rate = createRate(
                1L,
                VehicleType.CAR,
                true
        );

        repository.save(rate);

        rate.setBaseAmount(75.0);
        rate.setExtraUnitAmount(35.0);

        repository.update(rate);

        ParkingRate updated =
                repository.findById(1L).orElseThrow();

        assertEquals(75.0, updated.getBaseAmount());
        assertEquals(35.0, updated.getExtraUnitAmount());
    }

    @Test
    void shouldDetectExistingActiveRate() {

        repository.save(
                createRate(
                        1L,
                        VehicleType.CAR,
                        true
                )
        );

        assertTrue(
                repository.existsActiveByVehicleType(
                        VehicleType.CAR
                )
        );
    }

    @Test
    void shouldAllowInactiveRateForVehicleType() {

        repository.save(
                createRate(
                        1L,
                        VehicleType.CAR,
                        false
                )
        );

        assertFalse(
                repository.existsActiveByVehicleType(
                        VehicleType.CAR
                )
        );
    }

    @Test
    void shouldKeepRatesInMemory() {

        ParkingRate rate = createRate(
                1L,
                VehicleType.CAR,
                true
        );

        repository.save(rate);

        assertEquals(
                1,
                dataStore.getParkingRates().size()
        );

        assertTrue(
                dataStore.getParkingRates()
                        .containsKey(1L)
        );
    }

    @Test
    void shouldRejectDuplicateActiveRateForSameVehicleType() {

        repository.save(
                createRate(
                        1L,
                        VehicleType.CAR,
                        true
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(
                        createRate(
                                2L,
                                VehicleType.CAR,
                                true
                        )
                )
        );
    }

    private ParkingRate createRate(
            Long id,
            VehicleType vehicleType,
            boolean active
    ) {
        return new ParkingRate(
                id,
                vehicleType,
                120,
                60.0,
                60,
                30.0,
                active,
                LocalDateTime.now()
        );
    }
}