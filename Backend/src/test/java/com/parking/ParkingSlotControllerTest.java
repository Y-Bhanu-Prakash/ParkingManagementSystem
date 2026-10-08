package com.parking;

import com.parking.entity.ParkingSlot;
import com.parking.entity.VehicleType;
import com.parking.repository.InMemoryDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ParkingSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore.getParkingSlots().clear();

        dataStore.getParkingSlots().put(
                1L,
                new ParkingSlot(
                        1L,
                        "C-001",
                        VehicleType.CAR,
                        ParkingSlot.Status.AVAILABLE,
                        true,
                        java.time.LocalDateTime.now()
                )
        );

        dataStore.getParkingSlots().put(
                2L,
                new ParkingSlot(
                        2L,
                        "B-001",
                        VehicleType.BIKE,
                        ParkingSlot.Status.AVAILABLE,
                        true,
                        java.time.LocalDateTime.now()
                )
        );
    }

    @Test
    void authenticatedStaffCanGetSlots() throws Exception {
        mockMvc.perform(
                get("/api/slots")
                        .with(user("attendant").roles("ATTENDANT"))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void ownerCanCreateSlot() throws Exception {
        String requestBody = """
                {
                    "slotCode": "C-002",
                    "slotType": "CAR"
                }
                """;

        mockMvc.perform(
                post("/api/slots")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.slotCode").value("C-002"))
        .andExpect(jsonPath("$.slotType").value("CAR"))
        .andExpect(jsonPath("$.status").value("AVAILABLE"))
        .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void attendantCannotCreateSlot() throws Exception {
        String requestBody = """
                {
                    "slotCode": "C-002",
                    "slotType": "CAR"
                }
                """;

        mockMvc.perform(
                post("/api/slots")
                        .with(user("attendant").roles("ATTENDANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void duplicateSlotCodeIsRejected() throws Exception {
        String requestBody = """
                {
                    "slotCode": "C-001",
                    "slotType": "CAR"
                }
                """;

        mockMvc.perform(
                post("/api/slots")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Slot code already exists"));
    }

    @Test
    void missingSlotCodeIsRejected() throws Exception {
        String requestBody = """
                {
                    "slotType": "CAR"
                }
                """;

        mockMvc.perform(
                post("/api/slots")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Slot code is required"));
    }

    @Test
    void missingSlotTypeIsRejected() throws Exception {
        String requestBody = """
                {
                    "slotCode": "C-002"
                }
                """;

        mockMvc.perform(
                post("/api/slots")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Slot type is required"));
    }

    @Test
    void ownerCanUpdateSlot() throws Exception {
        String requestBody = """
                {
                    "slotCode": "C-010",
                    "slotType": "SUV"
                }
                """;

        mockMvc.perform(
                put("/api/slots/1")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.slotCode").value("C-010"))
        .andExpect(jsonPath("$.slotType").value("SUV"));
    }

    @Test
    void attendantCannotUpdateSlot() throws Exception {
        String requestBody = """
                {
                    "slotCode": "C-010",
                    "slotType": "SUV"
                }
                """;

        mockMvc.perform(
                put("/api/slots/1")
                        .with(user("attendant").roles("ATTENDANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void invalidSlotIdIsRejected() throws Exception {
        String requestBody = """
                {
                    "slotCode": "C-010",
                    "slotType": "CAR"
                }
                """;

        mockMvc.perform(
                put("/api/slots/999")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error")
                .value("Parking slot not found with ID: 999"));
    }

    @Test
    void ownerCanChangeSlotStatus() throws Exception {
        String requestBody = """
                {
                    "status": "RESERVED"
                }
                """;

        mockMvc.perform(
                patch("/api/slots/1/status")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("RESERVED"));
    }

    @Test
    void attendantCannotChangeSlotStatus() throws Exception {
        String requestBody = """
                {
                    "status": "RESERVED"
                }
                """;

        mockMvc.perform(
                patch("/api/slots/1/status")
                        .with(user("attendant").roles("ATTENDANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void ownerCanDeactivateSlot() throws Exception {
        String requestBody = """
                {
                    "active": false
                }
                """;

        mockMvc.perform(
                patch("/api/slots/1/status")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void ownerCanActivateSlot() throws Exception {
        dataStore.getParkingSlots().get(1L);

        ParkingSlot slot =
                (ParkingSlot) dataStore.getParkingSlots().get(1L);

        slot.setActive(false);

        String requestBody = """
                {
                    "active": true
                }
                """;

        mockMvc.perform(
                patch("/api/slots/1/status")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void occupiedSlotCannotBeDeactivated() throws Exception {
        ParkingSlot slot =
                (ParkingSlot) dataStore.getParkingSlots().get(1L);

        slot.setStatus(ParkingSlot.Status.OCCUPIED);

        String requestBody = """
                {
                    "active": false
                }
                """;

        mockMvc.perform(
                patch("/api/slots/1/status")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error")
                .value("Cannot deactivate slot while it has an active parking session"));
    }

    @Test
    void occupiedSlotCannotBeChangedToMaintenance() throws Exception {
        ParkingSlot slot =
                (ParkingSlot) dataStore.getParkingSlots().get(1L);

        slot.setStatus(ParkingSlot.Status.OCCUPIED);

        String requestBody = """
                {
                    "status": "MAINTENANCE"
                }
                """;

        mockMvc.perform(
                patch("/api/slots/1/status")
                        .with(user("owner").roles("OWNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error")
                .value("Cannot change slot to MAINTENANCE while it has an active parking session"));
    }
}