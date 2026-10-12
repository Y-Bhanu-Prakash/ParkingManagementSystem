
package com.parking.service;

import com.parking.entity.ParkingSession;
import java.time.LocalDateTime;

public record VehicleEntryResponse(
        ParkingSession session,
        String passCode,
        String password,
        LocalDateTime passIssuedAt
) {
    
}