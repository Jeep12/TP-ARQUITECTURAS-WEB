package com.terra.team.prural.auth.dto.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    // Constructors
    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
