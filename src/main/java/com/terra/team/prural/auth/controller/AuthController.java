package com.terra.team.prural.auth.controller;

import com.terra.team.prural.auth.dto.register.RegisterRequest;
import com.terra.team.prural.auth.dto.register.RegisterResponse;
import com.terra.team.prural.auth.dto.verification.VerifyEmailResponse;
import com.terra.team.prural.auth.dto.verification.ResendVerificationResponse;
import com.terra.team.prural.auth.dto.password.PasswordResetResponse;
import com.terra.team.prural.auth.dto.password.ForgotPasswordRequest;
import com.terra.team.prural.auth.dto.password.ResetPasswordRequest;
import com.terra.team.prural.auth.dto.logout.LogoutRequest;
import com.terra.team.prural.auth.dto.logout.LogoutResponse;
import com.terra.team.prural.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    

    
    @GetMapping("/verify")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestParam String token) {
        VerifyEmailResponse response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<ResendVerificationResponse> resendVerificationEmail(@RequestBody ForgotPasswordRequest request) {
        ResendVerificationResponse response = authService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        PasswordResetResponse response = authService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> resetPassword(@RequestParam String token, @Valid @RequestBody ResetPasswordRequest request) {
        PasswordResetResponse response = authService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@CookieValue("refresh_token") String refreshToken) {
        try {
            Map<String, String> response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of(
                "message", "Token de refresh inválido o expirado",
                "error", "REFRESH_TOKEN_INVALID"
            );
            return ResponseEntity.status(401).body(error);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@CookieValue("refresh_token") String refreshToken) {
        LogoutResponse response = authService.logout(refreshToken);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth service is working!");
    }
}
