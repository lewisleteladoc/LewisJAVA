package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class TokenService {

    @Value("${jwt.secret-key}")
    private String secretKeyBase64;

    private SecretKey key;

    // Build the key once at startup — not on every request
    @PostConstruct
    private void initKey() {
        if (secretKeyBase64 == null || secretKeyBase64.isBlank()) {
            throw new IllegalStateException(
                "jwt.secret-key is not configured. " +
                "Set it in application-local.properties for local dev."
            );
        }
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
            this.key = Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                "jwt.secret-key is not valid Base64: " + e.getMessage()
            );
        }
    }

    /**
     * Validates the token and returns its scopes.
     * Throws a specific exception on any validation failure — never swallows errors silently.
     */
    public List<String> getScopes(String token) {
        Claims claims = parseAndValidate(token); // throws on any invalid state

        Object scopeClaim = claims.get("scope");

        if (scopeClaim instanceof List<?> scopeList) {
            // Safe element-by-element cast instead of raw unchecked cast
            return scopeList.stream()
                    .filter(s -> s instanceof String)
                    .map(s -> (String) s)
                    .toList();
        }
        if (scopeClaim instanceof String scopeString) {
            return List.of(scopeString.split(" "));
        }

        return Collections.emptyList(); // valid token, but no scope claim present
    }
    
    public String getSub(String token) {
        Claims claims = parseAndValidate(token); // throws on any invalid state

        Object sub = claims.get("sub");

        return sub instanceof String ? (String) sub : null; // valid token, but no sub claim present
    }
    /**
     * Exposes raw claims for callers that need more than just scopes
     * (e.g. username, roles, custom claims).
     */
    public Claims getClaims(String token) {
        return parseAndValidate(token);
    }

    // --- private ---

    private Claims parseAndValidate(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer("VM02_DA_NANG")
                    .requireAudience("MyClient")
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new TokenException("Token has expired", e);
        } catch (JwtException e) {
            throw new TokenException("Token is invalid: " + e.getMessage(), e);
        }
        // Let unexpected exceptions (e.g. NullPointerException) propagate naturally
    }

    /** Typed exception so callers can catch token failures specifically. */
    public static class TokenException extends RuntimeException {
        public TokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}