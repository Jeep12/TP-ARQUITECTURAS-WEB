package com.terra.team.prural.auth.dto.logout;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    
    @NotBlank(message = "El refresh token es requerido")
    private String refreshToken;
    
    // Constructors
    public LogoutRequest() {}
    
    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
