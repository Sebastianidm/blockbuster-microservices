package com.blockbuster.catalog.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilsTest {

    private static final String SECRET = "catalog-secret-key-with-32-characters-min";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET);
    }

    @Test
    void shouldExtractUsernameAndRole() {
        String token = Jwts.builder()
                .setSubject("admin")
                .claim("role", "ROLE_ADMIN")
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        assertEquals("admin", jwtUtils.extractUsername(token));
        assertEquals("ROLE_ADMIN", jwtUtils.extractRole(token));
    }

    @Test
    void shouldValidateTokenForMatchingUser() {
        String token = Jwts.builder()
                .setSubject("martin")
                .claim("role", "ROLE_USER")
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        UserDetails userDetails = User.builder()
                .username("martin")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        assertTrue(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        String token = Jwts.builder()
                .setSubject("admin")
                .claim("role", "ROLE_ADMIN")
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        UserDetails userDetails = User.builder()
                .username("martin")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        assertFalse(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        String token = Jwts.builder()
                .setSubject("martin")
                .claim("role", "ROLE_USER")
                .setExpiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        UserDetails userDetails = User.builder()
                .username("martin")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        assertThrows(ExpiredJwtException.class, () -> jwtUtils.isTokenValid(token, userDetails));
    }
}
