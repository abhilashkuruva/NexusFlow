package com.nexusflow.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private Long expirationMs;

    /**
     * Generate signing key
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT token
     */
    public String generateToken(String email, Long userId) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("email", email);
        claims.put("userId", userId);

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract all claims
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract email
     */
    public String extractEmail(String token) {

        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract user ID
     */
    public Long extractUserId(String token) {

        Object userId = extractAllClaims(token).get("userId");

        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }

        return (Long) userId;
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {

        try {

            extractAllClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Check token expiration
     */
    public boolean isTokenExpired(String token) {

        try {

            Date expiration =
                    extractAllClaims(token).getExpiration();

            return expiration.before(new Date());

        } catch (Exception e) {

            return true;
        }
    }

    /**
     * Get expiration time
     */
    public Long getExpirationMs() {

        return expirationMs;
    }
}