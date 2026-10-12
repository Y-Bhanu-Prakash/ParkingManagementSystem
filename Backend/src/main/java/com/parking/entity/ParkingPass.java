
package com.parking.entity;

import java.time.LocalDateTime;

public class ParkingPass {

    private Long id;
    private String passCode;
    private Long sessionId;
    private String passwordHash;
    private LocalDateTime issuedAt;
    private boolean active;

    public ParkingPass() {
    }

    public ParkingPass(
            Long id,
            String passCode,
            Long sessionId,
            String passwordHash,
            LocalDateTime issuedAt,
            boolean active
    ) {
        this.id = id;
        this.passCode = passCode;
        this.sessionId = sessionId;
        this.passwordHash = passwordHash;
        this.issuedAt = issuedAt;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
