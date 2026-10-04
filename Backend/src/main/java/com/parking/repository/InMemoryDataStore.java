package com.parking.repository;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryDataStore {

    private final Map<Long, Object> users = new ConcurrentHashMap<>();

    private final Map<Long, Object> parkingSlots = new ConcurrentHashMap<>();

    private final Map<Long, Object> parkingSessions = new ConcurrentHashMap<>();

    private final Map<Long, Object> parkingPasses = new ConcurrentHashMap<>();

    private final Map<Long, Object> payments = new ConcurrentHashMap<>();

    private final Map<Long, Object> notifications = new ConcurrentHashMap<>();

    private final Map<Long, Object> parkingHistory = new ConcurrentHashMap<>();

    public Map<Long, Object> getUsers() {
        return users;
    }

    public Map<Long, Object> getParkingSlots() {
        return parkingSlots;
    }

    public Map<Long, Object> getParkingSessions() {
        return parkingSessions;
    }

    public Map<Long, Object> getParkingPasses() {
        return parkingPasses;
    }

    public Map<Long, Object> getPayments() {
        return payments;
    }

    public Map<Long, Object> getNotifications() {
        return notifications;
    }

    public Map<Long, Object> getParkingHistory() {
        return parkingHistory;
    }
}