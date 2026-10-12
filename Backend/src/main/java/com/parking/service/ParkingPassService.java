
package com.parking.service;

import com.parking.entity.ParkingPass;
import com.parking.repository.ParkingPassRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ParkingPassService {

    private final ParkingPassRepository parkingPassRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ParkingPassService(
            ParkingPassRepository parkingPassRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.parkingPassRepository = parkingPassRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public GeneratedParkingPass generatePass(Long sessionId) {

        if (sessionId == null) {
            throw new IllegalArgumentException(
                    "Parking session ID is required"
            );
        }

        if (parkingPassRepository.existsBySessionId(sessionId)) {
            throw new IllegalArgumentException(
                    "A parking pass already exists for session: "
                            + sessionId
            );
        }

        String passCode = generateUniquePassCode();
        String password = generateFourDigitPassword();
        String passwordHash = passwordEncoder.encode(password);

        ParkingPass parkingPass = new ParkingPass(
                idGenerator.getAndIncrement(),
                passCode,
                sessionId,
                passwordHash,
                LocalDateTime.now(),
                true
        );

        parkingPassRepository.save(parkingPass);

        // Return the plain password only in this creation result.
        // Never include the stored hash in this result.
        return new GeneratedParkingPass(
                parkingPass.getId(),
                parkingPass.getPassCode(),
                parkingPass.getSessionId(),
                password,
                parkingPass.getIssuedAt(),
                parkingPass.isActive()
        );
    }

    public boolean verifyPassword(
            String passCode,
            String suppliedPassword
    ) {
        if (passCode == null
                || passCode.isBlank()
                || suppliedPassword == null
                || !suppliedPassword.matches("\\d{4}")) {
            return false;
        }

        return parkingPassRepository.findByPassCode(passCode)
                .filter(ParkingPass::isActive)
                .map(pass ->
                        passwordEncoder.matches(
                                suppliedPassword,
                                pass.getPasswordHash()
                        )
                )
                .orElse(false);
    }

    private String generateUniquePassCode() {
        String passCode;

        do {
            passCode = "PASS-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();
        } while (parkingPassRepository.existsByPassCode(passCode));

        return passCode;
    }

    private String generateFourDigitPassword() {
        return String.format(
                "%04d",
                secureRandom.nextInt(10_000)
        );
    }

    public record GeneratedParkingPass(
            Long id,
            String passCode,
            Long sessionId,
            String password,
            LocalDateTime issuedAt,
            boolean active
    ) {
    }
}
