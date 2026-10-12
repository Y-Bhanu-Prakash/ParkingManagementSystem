package com.parking.repository;

import com.parking.entity.ParkingPass;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ParkingPassRepository {

    private final InMemoryDataStore dataStore;

    public ParkingPassRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public ParkingPass save(ParkingPass parkingPass) {

        if (parkingPass == null) {
            throw new IllegalArgumentException(
                    "Parking pass is required"
            );
        }

        if (parkingPass.getId() == null) {
            throw new IllegalArgumentException(
                    "Parking pass ID is required"
            );
        }

        if (parkingPass.getPassCode() == null
                || parkingPass.getPassCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Pass code is required"
            );
        }

        Optional<ParkingPass> existingPass =
                findByPassCode(parkingPass.getPassCode());

        if (existingPass.isPresent()
                && !existingPass.get().getId()
                        .equals(parkingPass.getId())) {
            throw new IllegalArgumentException(
                    "Pass code already exists"
            );
        }

        dataStore.getParkingPasses().put(
                parkingPass.getId(),
                parkingPass
        );

        return parkingPass;
    }

    public Optional<ParkingPass> findById(Long id) {

        Object pass = dataStore.getParkingPasses().get(id);

        if (pass instanceof ParkingPass) {
            return Optional.of((ParkingPass) pass);
        }

        return Optional.empty();
    }

    public Optional<ParkingPass> findByPassCode(
            String passCode
    ) {

        if (passCode == null || passCode.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(pass ->
                        pass.getPassCode().equals(passCode)
                )
                .findFirst();
    }

    public Optional<ParkingPass> findBySessionId(
            Long sessionId
    ) {

        if (sessionId == null) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(pass ->
                        pass.getSessionId().equals(sessionId)
                )
                .findFirst();
    }

    public List<ParkingPass> findAll() {

        return dataStore.getParkingPasses()
                .values()
                .stream()
                .filter(ParkingPass.class::isInstance)
                .map(ParkingPass.class::cast)
                .toList();
    }

    public List<ParkingPass> findActivePasses() {

        return findAll()
                .stream()
                .filter(ParkingPass::isActive)
                .toList();
    }

    public ParkingPass update(ParkingPass parkingPass) {

        if (parkingPass == null
                || parkingPass.getId() == null) {
            throw new IllegalArgumentException(
                    "Parking pass and ID are required"
            );
        }

        if (!dataStore.getParkingPasses()
                .containsKey(parkingPass.getId())) {
            throw new IllegalArgumentException(
                    "Parking pass not found"
            );
        }

        return save(parkingPass);
    }

    public boolean existsByPassCode(String passCode) {
        return findByPassCode(passCode).isPresent();
    }

    public boolean existsBySessionId(Long sessionId) {
        return findBySessionId(sessionId).isPresent();
    }
}