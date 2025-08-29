package com.terra.team.prural.auth.dto.phone;

public class DeletePhoneResponse {
    
    private String message;
    private boolean success;
    private Long phoneId;
    
    // Constructors
    public DeletePhoneResponse() {}
    
    public DeletePhoneResponse(String message, boolean success, Long phoneId) {
        this.message = message;
        this.success = success;
        this.phoneId = phoneId;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Long getPhoneId() {
        return phoneId;
    }
    
    public void setPhoneId(Long phoneId) {
        this.phoneId = phoneId;
    }
}
