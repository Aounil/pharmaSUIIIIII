package com.pharmacy.shared.security;

import com.pharmacy.shared.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms:${app.jwt.expiration:86400000}}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        return generateToken(user, null);
    }

    public String generateToken(User user, Long pharmacyId) {
        var builder = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", List.of(user.getRole()))
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs));

        if (pharmacyId != null) {
            builder.claim("pharmacyId", pharmacyId);
        }

        return builder.signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    public String extractEmail(String token) {
        return parseToken(token).getSubject();
    }

    public Long extractUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public Long extractPharmacyId(String token) {
        return parseToken(token).get("pharmacyId", Long.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) parseToken(token).get("roles");
    }
}
