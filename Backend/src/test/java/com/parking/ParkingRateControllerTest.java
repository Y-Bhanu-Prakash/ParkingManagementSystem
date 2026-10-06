package com.parking;

import com.parking.dto.ParkingRateUpdateRequest;
import com.parking.entity.ParkingRate;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ParkingRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore.getParkingRates().clear();

        dataStore.getParkingRates().put(
                1L,
                new ParkingRate(
                        1L,
                        VehicleType.CAR,
                        120,
                        60.0,
                        60,
                        30.0,
                        LocalDateTime.now().minusMinutes(10)
                )
        );

        dataStore.getParkingRates().put(
                2L,
                new ParkingRate(
                        2L,
                        VehicleType.BIKE,
                        120,
                        30.0,
                        60,
                        10.0,
                        LocalDateTime.now().minusMinutes(10)
                )
        );
    }

    @Test
    void authenticatedStaffCanGetRates() throws Exception {
        mockMvc.perform(
                get("/api/rates")
                        .with(user("attendant").roles("ATTENDANT"))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void ownerCanUpdateRate() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "CAR",
                    "baseDurationMinutes": 180,
                    "baseAmount": 90.0,
                    "extraUnitMinutes": 60,
                    "extraAmount": 40.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.vehicleType").value("CAR"))
        .andExpect(jsonPath("$.baseDurationMinutes").value(180))
        .andExpect(jsonPath("$.baseAmount").value(90.0))
        .andExpect(jsonPath("$.extraUnitMinutes").value(60))
        .andExpect(jsonPath("$.extraAmount").value(40.0))
        .andExpect(jsonPath("$.effectiveFrom").exists());
    }

    @Test
    void attendantCannotUpdateRate() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "CAR",
                    "baseDurationMinutes": 180,
                    "baseAmount": 90.0,
                    "extraUnitMinutes": 60,
                    "extraAmount": 40.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("attendant").roles("ATTENDANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void invalidBaseDurationIsRejected() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "CAR",
                    "baseDurationMinutes": 0,
                    "baseAmount": 90.0,
                    "extraUnitMinutes": 60,
                    "extraAmount": 40.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void invalidExtraDurationIsRejected() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "CAR",
                    "baseDurationMinutes": 120,
                    "baseAmount": 90.0,
                    "extraUnitMinutes": 0,
                    "extraAmount": 40.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void negativeBaseAmountIsRejected() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "CAR",
                    "baseDurationMinutes": 120,
                    "baseAmount": -1.0,
                    "extraUnitMinutes": 60,
                    "extraAmount": 40.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void negativeExtraAmountIsRejected() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "CAR",
                    "baseDurationMinutes": 120,
                    "baseAmount": 90.0,
                    "extraUnitMinutes": 60,
                    "extraAmount": -1.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void duplicateActiveVehicleTypeIsRejected() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "BIKE",
                    "baseDurationMinutes": 120,
                    "baseAmount": 70.0,
                    "extraUnitMinutes": 60,
                    "extraAmount": 30.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void invalidVehicleTypeIsRejected() throws Exception {
        String requestBody = """
                {
                    "vehicleType": "BUS",
                    "baseDurationMinutes": 120,
                    "baseAmount": 90.0,
                    "extraUnitMinutes": 60,
                    "extraAmount": 40.0
                }
                """;

        mockMvc.perform(
                put("/api/rates/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest());
    }
}