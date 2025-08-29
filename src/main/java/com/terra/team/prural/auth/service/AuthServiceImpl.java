package com.terra.team.prural.auth.service;

import com.terra.team.prural.auth.dto.register.RegisterRequest;
import com.terra.team.prural.auth.dto.register.RegisterResponse;
import com.terra.team.prural.auth.dto.verification.VerifyEmailResponse;
import com.terra.team.prural.auth.dto.verification.ResendVerificationResponse;
import com.terra.team.prural.auth.dto.password.PasswordResetResponse;
import com.terra.team.prural.auth.dto.logout.LogoutResponse;
import com.terra.team.prural.auth.entity.Role;
import com.terra.team.prural.auth.entity.User;
import com.terra.team.prural.auth.repository.RoleRepository;
import com.terra.team.prural.auth.repository.UserRepository;
import com.terra.team.prural.utils.CodeGenerator;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import io.jsonwebtoken.Claims;
import com.terra.team.prural.security.config.TokenJwtConfig;
import io.jsonwebtoken.Jwts;

@Service
public class AuthServiceImpl implements AuthService {

    // Blacklist para tokens invalidados por logout
    private static final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.terra.team.prural.email.service.EmailService emailService;

    @Autowired
    private TokenJwtConfig tokenJwtConfig;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new RegisterResponse("El email ya está registrado", false);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setLastname(request.getLastname());
        user.setEnabled(true);
        user.setEmailVerified(false);

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Rol CLIENT no encontrado"));
        user.setRoles(Arrays.asList(clientRole));

        String verificationToken = CodeGenerator.generateUUIDToken();
        user.setVerificationToken(verificationToken);

        // Expiración 15 minutos
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        user.setTokenExpirationTime(cal.getTime());

        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }

        return new RegisterResponse("Usuario registrado exitosamente. Por favor verifica tu email.", true);
    }

    @Override
    public VerifyEmailResponse verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationTokenAndNotExpired(token, new Date());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmailVerified(true);
            user.setEnabled(true);
            user.setVerificationToken(null);
            user.setTokenExpirationTime(null);
            userRepository.save(user);
            return new VerifyEmailResponse("Email verificado exitosamente", true);
        } else {
            return new VerifyEmailResponse("Token inválido o expirado", false);
        }
    }

    @Override
    public ResendVerificationResponse resendVerificationEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.isEmailVerified()) {
                return new ResendVerificationResponse("El email ya está verificado", false);
            }

            // Verificar si ya existe un token válido
            if (user.getVerificationToken() != null && user.getTokenExpirationTime() != null) {
                Date now = new Date();
                if (user.getTokenExpirationTime().after(now)) {
                    // Calcular tiempo restante en minutos
                    long timeRemaining = (user.getTokenExpirationTime().getTime() - now.getTime()) / (1000 * 60);
                    return new ResendVerificationResponse(
                        "Ya existe un token de verificación válido. Espera " + timeRemaining + " minutos antes de solicitar otro.", 
                        false
                    );
                }
            }

            String verificationToken = CodeGenerator.generateUUIDToken();
            user.setVerificationToken(verificationToken);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 15);
            user.setTokenExpirationTime(cal.getTime());

            userRepository.save(user);

            try {
                emailService.sendVerificationEmail(user.getEmail(), verificationToken);
                return new ResendVerificationResponse("Email de verificación reenviado", true);
            } catch (MessagingException | UnsupportedEncodingException e) {
                return new ResendVerificationResponse("Error al enviar el email de verificación", false);
            }
        } else {
            return new ResendVerificationResponse("Usuario no encontrado", false);
        }
    }

    @Override
    public PasswordResetResponse sendPasswordResetEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (!user.isEmailVerified()) {
                return new PasswordResetResponse("El email no está verificado", false);
            }

            // Verificar si ya existe un token válido
            if (user.getResetPasswordToken() != null && user.getResetPasswordTokenExpiration() != null) {
                Date now = new Date();
                if (user.getResetPasswordTokenExpiration().after(now)) {
                    // Calcular tiempo restante en minutos
                    long timeRemaining = (user.getResetPasswordTokenExpiration().getTime() - now.getTime()) / (1000 * 60);
                    return new PasswordResetResponse(
                        "Ya existe un token de reset válido. Espera " + timeRemaining + " minutos antes de solicitar otro.", 
                        false
                    );
                }
            }

            String resetToken = CodeGenerator.generateUUIDToken();
            user.setResetPasswordToken(resetToken);

            // Expiración 1 hora
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            user.setResetPasswordTokenExpiration(cal.getTime());

            userRepository.save(user);

            try {
                emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
                return new PasswordResetResponse("Email de restablecimiento de contraseña enviado", true);
            } catch (MessagingException | UnsupportedEncodingException e) {
                return new PasswordResetResponse("Error al enviar el email de restablecimiento", false);
            }
        } else {
            return new PasswordResetResponse("Usuario no encontrado", false);
        }
    }

    @Override
    public PasswordResetResponse resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verificar si el token no ha expirado
            if (user.getResetPasswordTokenExpiration() != null && 
                user.getResetPasswordTokenExpiration().after(new Date())) {
                
                // Cambiar la contraseña
                user.setPassword(passwordEncoder.encode(newPassword));
                
                // Limpiar el token de reset
                user.setResetPasswordToken(null);
                user.setResetPasswordTokenExpiration(null);
                
                userRepository.save(user);
                
                return new PasswordResetResponse("Contraseña cambiada exitosamente", true);
            } else {
                return new PasswordResetResponse("El token de reset ha expirado", false);
            }
        } else {
            return new PasswordResetResponse("Token de reset inválido", false);
        }
    }
    
    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        try {
            // Verificar si el token está en la blacklist
            if (blacklistedTokens.contains(refreshToken)) {
                throw new RuntimeException("Token invalidado por logout");
            }
            
            // Validar el refresh token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(tokenJwtConfig.getSecretKey())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
            
            // Verificar que sea un refresh token
            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                throw new RuntimeException("Token no es de tipo refresh");
            }
            
            // Verificar que no haya expirado
            if (claims.getExpiration().before(new Date())) {
                throw new RuntimeException("Refresh token expirado");
            }
            
            // Obtener email del usuario
            String email = claims.getSubject();
            
            // Buscar usuario para verificar que existe y está activo
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty() || !userOpt.get().isEnabled() || !userOpt.get().isEmailVerified()) {
                throw new RuntimeException("Usuario no válido");
            }
            
            // Generar nuevo access token
            List<String> roles = userOpt.get().getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());
            
            String newAccessToken = Jwts.builder()
                    .setSubject(email)
                    .claim("authorities", roles)
                    .setExpiration(new Date(System.currentTimeMillis() + tokenJwtConfig.getAccessTokenExpiration()))
                    .signWith(tokenJwtConfig.getSecretKey())
                    .compact();
            
            // Retornar el nuevo token
            return Map.of(
                "access_token", newAccessToken,
                "message", "Token renovado exitosamente"
            );

            
        } catch (Exception e) {
            throw new RuntimeException("Error renovando token: " + e.getMessage());
        }
    }
    
    @Override
    public LogoutResponse logout(String refreshToken) {
        try {
            // Validar el refresh token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(tokenJwtConfig.getSecretKey())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
            
            // Verificar que sea un refresh token
            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                return new LogoutResponse("Token no válido", false);
            }
            
            // Agregar el token a la blacklist para invalidarlo
            blacklistedTokens.add(refreshToken);
            
            return new LogoutResponse("Logout exitoso", true);
            
        } catch (Exception e) {
            return new LogoutResponse("Error en logout: " + e.getMessage(), false);
        }
    }
}
