package com.ddoganzip.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String email, String role) {
        log.debug("[JwtUtil] Generating access token for email: {} with role: {}", email, role);
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)  // role 정보 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        log.info("[JwtUtil] Access token generated successfully for email: {}, role: {}", email, role);
        return token;
    }

    public String generateRefreshToken(String email, String role) {
        log.debug("[JwtUtil] Generating refresh token for email: {} with role: {}", email, role);
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)  // role 정보 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        log.info("[JwtUtil] Refresh token generated successfully for email: {}, role: {}", email, role);
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            log.debug("[JwtUtil] Token validation successful");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[JwtUtil] Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String email = claims.getSubject();
        log.debug("[JwtUtil] Extracted email from token: {}", email);
        return email;
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String role = claims.get("role", String.class);
        log.debug("[JwtUtil] Extracted role from token: {}", role);
        return role;
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public long getAccessTokenValidity() {
        return accessTokenValidity;
    }
}
