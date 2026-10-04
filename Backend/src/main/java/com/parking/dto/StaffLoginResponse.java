package com.parking.dto;

import com.parking.entity.User;

public class StaffLoginResponse {

    private Long userId;
    private String name;
    private String username;
    private User.Role role;

    public StaffLoginResponse() {
    }

    public StaffLoginResponse(
            Long userId,
            String name,
            String username,
            User.Role role
    ) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }
}