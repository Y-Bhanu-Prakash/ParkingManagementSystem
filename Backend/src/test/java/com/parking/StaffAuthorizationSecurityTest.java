package com.parking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(StaffAuthorizationTestController.class)
public class StaffAuthorizationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ownerCanAccessOwnerEndpoint() throws Exception {

        mockMvc.perform(
                get("/api/owner/test")
                        .with(user("owner").roles("OWNER"))
        )
        .andExpect(status().isOk());
    }

    @Test
    void attendantCannotAccessOwnerEndpoint() throws Exception {

        mockMvc.perform(
                get("/api/owner/test")
                        .with(user("attendant").roles("ATTENDANT"))
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void attendantCanAccessAttendantEndpoint() throws Exception {

        mockMvc.perform(
                get("/api/attendant/test")
                        .with(user("attendant").roles("ATTENDANT"))
        )
        .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedUserCannotAccessOwnerEndpoint() throws Exception {

        mockMvc.perform(
                get("/api/owner/test")
        )
        .andExpect(status().isUnauthorized());
    }
}