package com.terra.team.prural.auth.dto.phone;

import com.terra.team.prural.auth.entity.PhoneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddPhoneRequest {
    
    @NotBlank(message = "El número de teléfono es requerido")
    private String phoneNumber;
    
    @NotNull(message = "El tipo de teléfono es requerido")
    private PhoneType phoneType;
    
    private boolean isPrimary = false;
    
    // Constructors
    public AddPhoneRequest() {}
    
    public AddPhoneRequest(String phoneNumber, PhoneType phoneType, boolean isPrimary) {
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
        this.isPrimary = isPrimary;
    }
    
    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public PhoneType getPhoneType() {
        return phoneType;
    }
    
    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }
    
    public boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
