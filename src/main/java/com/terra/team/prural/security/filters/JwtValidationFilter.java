package com.terra.team.prural.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terra.team.prural.security.config.TokenJwtConfig;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Este filtro se ejecuta en cada petición HTTP para validar el JWT
 * 
 * Lo que hace:
 * 1. Ve si la ruta necesita autenticación o no
 * 2. Si necesita, saca el token de las cookies
 * 3. Valida que el token sea válido y no haya expirado
 * 4. Si todo está bien, establece el contexto de seguridad
 * 5. Si algo falla, envía un error apropiado
 */
public class JwtValidationFilter extends BasicAuthenticationFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);

    // Configuración de JWT (clave secreta, etc.)
    private final TokenJwtConfig tokenJwtConfig;

    /**
     * Constructor - acá recibo lo que necesito para validar tokens
     */
    public JwtValidationFilter(AuthenticationManager authManager, TokenJwtConfig tokenJwtConfig) {
        super(authManager);
        this.tokenJwtConfig = tokenJwtConfig;
    }

    /**
     * Acá decido si una petición debe ser filtrada o no
     * Si es una ruta pública, no la filtro
     * Si es una ruta privada, sí la filtro
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Estas son las rutas que NO necesitan validación de JWT
        // Cualquiera puede acceder a estas sin estar logueado
        String[] publicPaths = {
            "/api/auth/login",           // Login de usuarios
            "/api/auth/register",        // Registro de usuarios
            "/api/auth/verify-email",    // Verificación de email (legacy)
            "/api/auth/resend-verification", // Reenvío de verificación
            "/api/auth/verify",          // Verificación de email
            "/api/auth/forgot-password", // Solicitud de reset de contraseña
            "/api/auth/reset-password",  // Reset de contraseña
            "/api/auth/logout",          // Logout (no necesita validación previa)
        };

        // Si la ruta coincide con alguna ruta pública, no la filtro
        for (String publicPath : publicPaths) {
            if (path.equals(publicPath)) {
                return true; // NO filtrar esta ruta
            }
        }

        return false; // SÍ filtrar esta ruta (requiere autenticación)
    }

    /**
     * Este es el método principal que se ejecuta para cada petición
     * Acá es donde valido el JWT y establezco la autenticación
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        // Primero saco el token de las cookies
        String token = obtenerTokenDeCookie(req);

        // Si no hay token, envío error de acceso denegado
        if (token == null || token.isBlank()) {
            logger.warn("❌ [JWT] No se recibió token.");
            sendError(res, HttpServletResponse.SC_FORBIDDEN, "No token provided", "Access denied", "NO_TOKEN");
            return;
        }

        try {
            // Ahora valido el JWT usando mi clave secreta
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(tokenJwtConfig.getSecretKey()) // Clave para verificar la firma
                    .build()
                    .parseClaimsJws(token) // Parseo y valido el token
                    .getBody(); // Obtengo el contenido del token

            // Extraigo los roles del token
            List<String> roles = (List<String>) claims.get("authorities");
            logger.debug("👮 [JWT] Roles del usuario: {}", roles);

            // Creo el objeto de autenticación con el email y los roles
            var auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),        // Email del usuario (subject)
                    null,                       // Credenciales (null porque ya está autenticado)
                    roles.stream()              // Convierto los roles String a SimpleGrantedAuthority
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );

            // Establezco la autenticación en el contexto de Spring Security
            // Esto hace que el usuario esté "logueado" para el resto de la petición
            SecurityContextHolder.getContext().setAuthentication(auth);
            logger.debug("✅ [JWT] Contexto de seguridad seteado correctamente.");
            
            // Ahora continúo con la cadena de filtros
            chain.doFilter(req, res);

        } catch (JwtException e) {
            // Si hay algún error con el JWT, lo manejo acá
            String code = e.getMessage();
            String message;
            String error;

            // Mapeo los códigos de error a mensajes legibles
            switch (code) {
                case "TOKEN_INACTIVE" -> {
                    message = "El token no está activo";
                    error = "Token inválido o deshabilitado";
                }
                case "TOKEN_EXPIRED" -> {
                    message = "El token expiró";
                    error = "Sesión caducada";
                }
                default -> {
                    message = "El token es inválido";
                    error = "No se pudo validar el token";
                    code = "INVALID_TOKEN";
                }
            }

            logger.error("❌ [JWT ERROR] {} - {}", code, error);
            sendError(res, HttpServletResponse.SC_UNAUTHORIZED, message, error, code);
        }
    }

    /**
     * Envío una respuesta de error en formato JSON
     * Acá es donde le digo al cliente qué salió mal
     */
    private void sendError(HttpServletResponse res, int status, String message, String error, String code)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        
        // Creo la respuesta JSON con la info del error
        new ObjectMapper().writeValue(res.getOutputStream(),
                Map.of(
                    "message", message,  // Mensaje que ve el usuario
                    "error", error,      // Descripción técnica del error
                    "code", code         // Código de error para debugging
                ));
    }

    /**
     * Busco el token JWT en las cookies de la petición
     * Si no lo encuentro, retorno null
     */
    private String obtenerTokenDeCookie(HttpServletRequest request) {
        // Primero verifico que existan cookies
        if (request.getCookies() != null) {
            // Busco la cookie que se llama "access_token"
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue(); // Retorno el valor del token
                }
            }
        }
        return null; // No encontré el token
    }
}
