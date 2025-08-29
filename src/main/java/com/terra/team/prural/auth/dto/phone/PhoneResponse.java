package com.terra.team.prural.auth.dto.phone;

import com.terra.team.prural.auth.entity.PhoneType;
import java.util.Date;

public class PhoneResponse {
    
    private Long id;
    private String phoneNumber;
    private PhoneType phoneType;
    private boolean isPrimary;
    private Date createdAt;
    
    // Constructors
    public PhoneResponse() {}
    
    public PhoneResponse(Long id, String phoneNumber, PhoneType phoneType, boolean isPrimary, Date createdAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
        this.isPrimary = isPrimary;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public boolean isPrimary() {
        return isPrimary;
    }
    
    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
