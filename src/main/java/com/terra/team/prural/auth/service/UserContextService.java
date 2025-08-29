package com.terra.team.prural.auth.service;

import com.terra.team.prural.auth.entity.User;
import com.terra.team.prural.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Obtiene el usuario autenticado actual desde el contexto de seguridad
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        String userEmail = authentication.getName();
        
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("Email de usuario no encontrado en el contexto de seguridad");
        }
        
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos con email: " + userEmail));
    }
    
    /**
     * Obtiene el ID del usuario autenticado actual
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    
    /**
     * Obtiene el email del usuario autenticado actual
     */
    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
