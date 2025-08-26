package com.gaprio.response;

import com.gaprio.entities.Role;
import lombok.Data;

import java.util.Date;

@Data
public class AuthResponse {
    private String token;
    private Date expiresAt;
    private String username;
    private Role role;

    public AuthResponse() {
    }

    public AuthResponse(String token, Date expiresAt, String username, Role role) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}