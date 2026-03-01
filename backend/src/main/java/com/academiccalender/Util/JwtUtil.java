package com.academiccalender.Util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey"; // min 256-bit
    private static final long EXPIRATION_TIME = 1000 * 60 * 60*5; // 1 hour

    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ✅ Generate token WITH userId + role
    public String generateToken(Long userId, String email, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role); // include role

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException e) {

            return false;
        }
    }

    // ✅ Extract email
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ Extract userId
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    // ✅ Extract role
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Helper method to parse claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
