
package com.parking;

import com.parking.entity.ParkingPass;
import com.parking.repository.InMemoryDataStore;
import com.parking.repository.ParkingPassRepository;
import com.parking.service.ParkingPassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class ParkingPassServiceTest {

    private InMemoryDataStore dataStore;
    private ParkingPassRepository parkingPassRepository;
    private ParkingPassService parkingPassService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        dataStore = new InMemoryDataStore();

        parkingPassRepository =
                new ParkingPassRepository(dataStore);

        passwordEncoder = new BCryptPasswordEncoder();

        parkingPassService = new ParkingPassService(
                parkingPassRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldGenerateParkingPass() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        assertNotNull(generated);
        assertNotNull(generated.id());
        assertNotNull(generated.passCode());
        assertEquals(100L, generated.sessionId());
        assertTrue(generated.active());
        assertNotNull(generated.issuedAt());
    }

    @Test
    void shouldGenerateFourDigitPassword() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        assertTrue(generated.password().matches("\\d{4}"));
    }

    @Test
    void shouldStorePasswordAsHash() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        ParkingPass storedPass =
                parkingPassRepository
                        .findByPassCode(generated.passCode())
                        .orElseThrow();

        assertNotEquals(
                generated.password(),
                storedPass.getPasswordHash()
        );

        assertTrue(
                passwordEncoder.matches(
                        generated.password(),
                        storedPass.getPasswordHash()
                )
        );
    }

    @Test
    void shouldVerifyCorrectPassword() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        assertTrue(
                parkingPassService.verifyPassword(
                        generated.passCode(),
                        generated.password()
                )
        );
    }

    @Test
    void shouldRejectIncorrectPassword() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        assertFalse(
                parkingPassService.verifyPassword(
                        generated.passCode(),
                        "0000".equals(generated.password())
                                ? "0001"
                                : "0000"
                )
        );
    }

    @Test
    void shouldRejectPasswordWithInvalidFormat() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        assertFalse(
                parkingPassService.verifyPassword(
                        generated.passCode(),
                        "12A4"
                )
        );
    }

    @Test
    void shouldGenerateUniquePassCodes() {
        ParkingPassService.GeneratedParkingPass first =
                parkingPassService.generatePass(100L);

        ParkingPassService.GeneratedParkingPass second =
                parkingPassService.generatePass(101L);

        assertNotEquals(first.passCode(), second.passCode());
    }

    @Test
    void shouldRejectDuplicatePassForSession() {
        parkingPassService.generatePass(100L);

        assertThrows(
                IllegalArgumentException.class,
                () -> parkingPassService.generatePass(100L)
        );
    }

    @Test
    void shouldRejectNullSessionId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> parkingPassService.generatePass(null)
        );
    }

    @Test
    void shouldFindPassBySessionId() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        ParkingPass storedPass =
                parkingPassRepository
                        .findBySessionId(100L)
                        .orElseThrow();

        assertEquals(generated.id(), storedPass.getId());
        assertEquals(100L, storedPass.getSessionId());
    }

    @Test
    void shouldNotVerifyPasswordForInactivePass() {
        ParkingPassService.GeneratedParkingPass generated =
                parkingPassService.generatePass(100L);

        ParkingPass storedPass =
                parkingPassRepository
                        .findByPassCode(generated.passCode())
                        .orElseThrow();

        storedPass.setActive(false);
        parkingPassRepository.update(storedPass);

        assertFalse(
                parkingPassService.verifyPassword(
                        generated.passCode(),
                        generated.password()
                )
        );
    }
}
