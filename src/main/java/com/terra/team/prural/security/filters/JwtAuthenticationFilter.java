package com.terra.team.prural.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terra.team.prural.auth.dto.login.LoginRequest;
import com.terra.team.prural.auth.entity.User;
import com.terra.team.prural.auth.repository.UserRepository;
import com.terra.team.prural.security.config.TokenJwtConfig;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Este filtro se encarga de todo el proceso de login
 * Cuando alguien hace POST a /api/auth/login, este filtro:
 * - Lee las credenciales del body
 * - Verifica que el email esté verificado
 * - Autentica al usuario
 * - Genera los tokens JWT
 * - Los mete en cookies seguras
 * - Responde con éxito o error
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // El manager de Spring que se encarga de autenticar
    private final AuthenticationManager authManager;
    
    // Para consultar usuarios y verificar si el email está verificado
    private final UserRepository userRepository;
    
    // Configuración de JWT (clave secreta, expiración, etc.)
    private final TokenJwtConfig tokenJwtConfig;

    /**
     * Constructor - acá configuro todo lo que necesito
     */
    public JwtAuthenticationFilter(AuthenticationManager authManager, UserRepository userRepository, TokenJwtConfig tokenJwtConfig) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.tokenJwtConfig = tokenJwtConfig;
        
        // Le digo a Spring que este filtro se active solo en /api/auth/login
        setFilterProcessesUrl("/api/auth/login");
    }

    /**
     * Acá es donde intento autenticar al usuario
     * Leo las credenciales del body y verifico que todo esté bien
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            // Leo el JSON del body y lo convierto a LoginRequest
            LoginRequest loginRequest = new ObjectMapper().readValue(req.getInputStream(), LoginRequest.class);

            // Antes de autenticar, verifico que el email esté verificado
            // Si no está verificado, no puede hacer login
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isPresent() && !userOpt.get().isEmailVerified()) {
                throw new BadCredentialsException("Email not verified.");
            }

            // Ahora sí, delego la autenticación a Spring Security
            // Este método verifica email/contraseña contra la BD
            return authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),    // Email del usuario
                            loginRequest.getPassword()  // Contraseña (en texto plano)
                    )
            );

        } catch (IOException e) {
            // Si hay error leyendo el body, lo logeo y lanzo excepción
            logger.error("❌ [LOGIN ERROR] Error leyendo credenciales: {}", e.getMessage(), e);
            throw new RuntimeException("Error leyendo credenciales", e);
        }
    }

    /**
     * Si la autenticación fue exitosa, acá genero los tokens y los meto en cookies
     * También envío la respuesta de éxito
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        // Extraigo la info del usuario que se autenticó
        String email = auth.getName();  // El email
        List<String> roles = auth.getAuthorities()  // Los roles
                .stream()
                .map(a -> a.getAuthority())  // Los convierto a String
                .collect(Collectors.toList());

        logger.info("✅ [LOGIN SUCCESS] Usuario autenticado: {}", email);

        // Genero el ACCESS TOKEN (el token principal)
        String token = Jwts.builder()
                .setSubject(email)                    // Email del usuario
                .claim("authorities", roles)          // Los roles que tiene
                .setExpiration(new Date(System.currentTimeMillis() + tokenJwtConfig.getAccessTokenExpiration()))  // Cuándo expira
                .signWith(tokenJwtConfig.getSecretKey())  // Lo firmo con mi clave secreta
                .compact();  // Y lo genero

        // Creo la cookie para el ACCESS TOKEN
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);     // Solo accesible por HTTP (no JavaScript)
        cookie.setPath("/");          // Disponible en toda la app
        cookie.setMaxAge((int) (tokenJwtConfig.getAccessTokenExpiration() / 1000));  // Expiración en segundos
        res.addCookie(cookie);        // La agrego a la respuesta

        // Ahora genero el REFRESH TOKEN (para renovar el access token cuando expire)
        String refreshToken = Jwts.builder()
                .setSubject(email)                    // Email del usuario
                .claim("type", "refresh")             // Le digo que es de tipo refresh
                .setExpiration(new Date(System.currentTimeMillis() + tokenJwtConfig.getRefreshTokenExpiration()))  // Expiración más larga
                .signWith(tokenJwtConfig.getSecretKey())  // Lo firmo con mi clave secreta
                .compact();  // Y lo genero

        // Creo la cookie para el REFRESH TOKEN
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);     // Solo accesible por HTTP
        refreshCookie.setPath("/");          // Disponible en toda la app
        refreshCookie.setMaxAge((int) (tokenJwtConfig.getRefreshTokenExpiration() / 1000));  // Expiración en segundos
        res.addCookie(refreshCookie);        // La agrego a la respuesta

        // Creo la respuesta JSON de éxito
        Map<String, String> body = Map.of(
                "message", "Authentication successful",  // Mensaje de éxito
                "email", email                          // Email del usuario autenticado
        );

        // Y la envío
        res.setContentType("application/json");
        new ObjectMapper().writeValue(res.getOutputStream(), body);
    }

    /**
     * Si la autenticación falló, acá manejo el error
     * Envío una respuesta apropiada según qué falló
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req,
                                              HttpServletResponse res,
                                              AuthenticationException failed) throws IOException {

        // Veo qué tipo de error fue para dar un mensaje apropiado
        String userMessage = "Credenciales Invalidas.";
        if ("Email not verified.".equals(failed.getMessage())) {
            userMessage = "Email no verificado.";
            logger.warn("❌ [LOGIN FAILED] Email not verified.");
        } else {
            logger.warn("❌ [LOGIN FAILED] Invalid credentials.");
        }

        // Configuro la respuesta de error
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Código 401
        
        // Creo el body JSON con la info del error
        Map<String, String> body = Map.of(
                "message", userMessage,  // Mensaje que ve el usuario
                "error", "LOGIN_FAILED"  // Código de error para debugging
        );
        
        // Y lo envío
        res.setContentType("application/json");
        new ObjectMapper().writeValue(res.getOutputStream(), body);
    }
}
