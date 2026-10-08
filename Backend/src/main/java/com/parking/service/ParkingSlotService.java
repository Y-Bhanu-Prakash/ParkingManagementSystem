package com.parking.service;

import com.parking.entity.ParkingSlot;

import java.util.List;

public interface ParkingSlotService {

    ParkingSlot createSlot(ParkingSlot slot);

    List<ParkingSlot> getAllSlots();

    ParkingSlot getSlotById(Long id);

    ParkingSlot updateSlot(Long id, ParkingSlot slot);

    ParkingSlot changeStatus(Long id, ParkingSlot.Status status);

    ParkingSlot activateSlot(Long id);

    ParkingSlot deactivateSlot(Long id);
}