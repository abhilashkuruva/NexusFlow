package com.nexusflow.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final long DEFAULT_EXPIRATION_TIME = 86400000; // 24 hours

    private final SecretKey key;
    private final long expirationTime;

    public JwtUtil(
            @Value("${jwt.secret:}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationTime
    ) {
        this.key = Keys.hmacShaKeyFor(normalizeSecret(secret));
        this.expirationTime = expirationTime > 0 ? expirationTime : DEFAULT_EXPIRATION_TIME;
    }

    public String generateToken(String email, Long userId, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .claim("email", email) // Add email as a claim for easier extraction
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            if (userId instanceof Number) {
                return ((Number) userId).longValue();
            }
            return null;
        });
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return (extractedEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private byte[] normalizeSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            byte[] generated = new byte[64];
            new SecureRandom().nextBytes(generated);
            return generated;
        }

        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        if (raw.length >= 32) {
            return raw;
        }

        try {
            return MessageDigest.getInstance("SHA-256").digest(raw);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to derive JWT signing key", e);
        }
    }
}
