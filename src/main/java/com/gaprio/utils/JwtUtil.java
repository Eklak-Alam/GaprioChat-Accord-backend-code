package com.gaprio.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key key;
    private final long jwtExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long jwtExpiration) {
        // Validate secret length for HS512
        if (secret.getBytes().length < 64) { // 64 bytes = 512 bits
            throw new IllegalArgumentException("JWT secret must be at least 512 bits (64 characters) for HS512 algorithm");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;

        System.out.println("✅ JWT Util initialized with secure key");
        System.out.println("✅ Key length: " + (secret.getBytes().length * 8) + " bits");
    }

    // 🔹 Generate token with subject (username)
    public String generateToken(String username) {
        try {
            String token = buildToken(username, jwtExpiration);
            System.out.println("✅ JWT Token generated for: " + username);
            return token;
        } catch (Exception e) {
            System.out.println("❌ Error generating token: " + e.getMessage());
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    // 🔹 Centralized token builder
    private String buildToken(String subject, long expirationMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // 🔹 Extract username (subject)
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            System.out.println("❌ Error extracting username from token: " + e.getMessage());
            return null;
        }
    }

    // 🔹 Extract expiration date
    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            System.out.println("❌ Error extracting expiration from token: " + e.getMessage());
            return null;
        }
    }

    // 🔹 Generic claim extractor
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = parseClaims(token);
        if (claims == null) {
            return null;
        }
        return resolver.apply(claims);
    }

    // 🔹 Validate token with username
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean isValid = (username.equals(extractedUsername) && !isTokenExpired(token));
            System.out.println("🔍 Token validation for " + username + ": " + isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ Token validation failed: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Check if token expired
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        if (expiration == null) {
            return true;
        }
        return expiration.before(new Date());
    }

    // 🔹 Parse claims safely
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Token expired: " + e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ Unsupported JWT: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            System.out.println("❌ Malformed JWT: " + e.getMessage());
            throw e;
        } catch (SecurityException e) {
            System.out.println("❌ JWT signature validation failed: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Illegal argument: " + e.getMessage());
            throw e;
        }
    }

    // 🔹 Expose expiration time
    public long getJwtExpiration() {
        return jwtExpiration;
    }

    // 🔹 Get key info for debugging
    public String getKeyAlgorithm() {
        return key.getAlgorithm();
    }
}