package com.terra.team.prural.auth.dto.verification;

public class ResendVerificationResponse {

    private String message;
    private boolean success;

    // Constructors
    public ResendVerificationResponse() {
    }

    public ResendVerificationResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
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
}
