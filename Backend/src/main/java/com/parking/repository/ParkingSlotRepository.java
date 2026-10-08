package com.parking.repository;

import com.parking.entity.ParkingSlot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ParkingSlotRepository {

    private final InMemoryDataStore dataStore;

    public ParkingSlotRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public ParkingSlot save(ParkingSlot slot) {
        dataStore.getParkingSlots().put(
                slot.getId(),
                slot
        );

        return slot;
    }

    public Optional<ParkingSlot> findById(Long id) {

        Object slot =
                dataStore.getParkingSlots().get(id);

        if (slot instanceof ParkingSlot) {
            return Optional.of((ParkingSlot) slot);
        }

        return Optional.empty();
    }

    public Optional<ParkingSlot> findBySlotCode(
            String slotCode
    ) {

        return findAll()
                .stream()
                .filter(slot ->
                        slot.getSlotCode()
                                .equalsIgnoreCase(slotCode)
                )
                .findFirst();
    }

    public List<ParkingSlot> findAll() {

        return dataStore.getParkingSlots()
                .values()
                .stream()
                .filter(ParkingSlot.class::isInstance)
                .map(ParkingSlot.class::cast)
                .toList();
    }

    public ParkingSlot update(ParkingSlot slot) {

        dataStore.getParkingSlots().put(
                slot.getId(),
                slot
        );

        return slot;
    }

    public boolean existsBySlotCode(
            String slotCode
    ) {

        return findBySlotCode(slotCode).isPresent();
    }
}