package com.terra.team.prural.auth.service;

import com.terra.team.prural.auth.dto.login.LoginRequest;
import com.terra.team.prural.auth.dto.register.RegisterRequest;
import com.terra.team.prural.auth.dto.register.RegisterResponse;
import com.terra.team.prural.auth.dto.verification.VerifyEmailResponse;
import com.terra.team.prural.auth.dto.verification.ResendVerificationResponse;
import com.terra.team.prural.auth.dto.password.PasswordResetResponse;
import com.terra.team.prural.auth.dto.logout.LogoutResponse;

import java.util.Map;

public interface AuthService {
    
    RegisterResponse register(RegisterRequest request);
    

    VerifyEmailResponse verifyEmail(String token);
    
    ResendVerificationResponse resendVerificationEmail(String email);
    
    PasswordResetResponse sendPasswordResetEmail(String email);
    
    PasswordResetResponse resetPassword(String token, String newPassword);
    
    Map<String, String> refreshToken(String refreshToken);
    
    LogoutResponse logout(String refreshToken);
}