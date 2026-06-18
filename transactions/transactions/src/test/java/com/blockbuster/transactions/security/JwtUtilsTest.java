package com.blockbuster.transactions.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class JwtUtilsTest {

    private static final String SECRET = "test-jwt-secret-with-at-least-32-characters";
    private final JwtUtils jwtUtils = new JwtUtils(SECRET);

    @Test
    void shouldExtractUsernameAndRoleFromToken() {
        String token = token("martin", "ROLE_ADMIN", new Date(System.currentTimeMillis() + 60_000));

        assertEquals("martin", jwtUtils.extractUsername(token));
        assertEquals("ROLE_ADMIN", jwtUtils.extractRole(token));
    }

    @Test
    void shouldValidateTokenWhenUsernameMatchesAndTokenIsNotExpired() {
        String token = token("martin", "ROLE_USER", new Date(System.currentTimeMillis() + 60_000));
        User user = new User("martin", "", java.util.List.of());

        assertTrue(jwtUtils.isTokenValid(token, user));
    }

    @Test
    void shouldInvalidateTokenWhenUsernameDoesNotMatch() {
        String token = token("martin", "ROLE_USER", new Date(System.currentTimeMillis() + 60_000));
        User user = new User("sebastian", "", java.util.List.of());

        assertFalse(jwtUtils.isTokenValid(token, user));
    }

    @Test
    void shouldThrowWhenTokenIsMalformed() {
        assertThrows(JwtException.class, () -> jwtUtils.extractUsername("not-a-jwt"));
    }

    private String token(String username, String role, Date expiration) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
