package com.parking.repository;

import com.parking.entity.ParkingSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ParkingSessionRepository {

    private final InMemoryDataStore dataStore;

    public ParkingSessionRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Create or store a parking session in memory.
     */
    public ParkingSession save(ParkingSession session) {
        dataStore.getParkingSessions().put(
                session.getId(),
                session
        );

        return session;
    }

    /**
     * Find a parking session by ID.
     */
    public Optional<ParkingSession> findById(Long id) {

        Object session =
                dataStore.getParkingSessions().get(id);

        if (session instanceof ParkingSession) {
            return Optional.of((ParkingSession) session);
        }

        return Optional.empty();
    }

    /**
     * Find all active parking sessions.
     */
    public List<ParkingSession> findActiveSessions() {

        return findAll()
                .stream()
                .filter(session ->
                        session.getStatus()
                                == ParkingSession.Status.ACTIVE
                )
                .toList();
    }

    /**
     * Find an active parking session
     * by vehicle number.
     */
    public Optional<ParkingSession> findActiveByVehicleNumber(
            String vehicleNumber
    ) {

        return findActiveSessions()
                .stream()
                .filter(session ->
                        session.getVehicleNumber()
                                .equalsIgnoreCase(vehicleNumber)
                )
                .findFirst();
    }

    /**
     * Find an active parking session
     * by parking slot ID.
     */
    public Optional<ParkingSession> findActiveBySlotId(
            Long slotId
    ) {

        return findActiveSessions()
                .stream()
                .filter(session ->
                        session.getSlotId().equals(slotId)
                )
                .findFirst();
    }

    /**
     * List all parking sessions.
     */
    public List<ParkingSession> findAll() {

        return dataStore.getParkingSessions()
                .values()
                .stream()
                .filter(ParkingSession.class::isInstance)
                .map(ParkingSession.class::cast)
                .toList();
    }

    /**
     * Update an existing parking session.
     */
    public ParkingSession update(ParkingSession session) {

        dataStore.getParkingSessions().put(
                session.getId(),
                session
        );

        return session;
    }
}