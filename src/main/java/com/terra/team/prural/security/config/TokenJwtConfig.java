package com.terra.team.prural.security.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class TokenJwtConfig {
    
    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;
    
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
    
    public static final String CONTENT_TYPE = "application/json";
}
