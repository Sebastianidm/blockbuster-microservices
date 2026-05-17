package com.blockbuster.users.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilsTest {

	private JwtUtils jwtUtils;

	@BeforeEach
	void setUp() {
		jwtUtils = new JwtUtils("test-secret-with-32-characters-minimum", 86400000L);
	}

	@Test
	void shouldGenerateTokenAndExtractClaims() {
		String token = jwtUtils.generateToken(15L, "martin", "ROLE_USER");

		assertNotNull(token);
		assertEquals("martin", jwtUtils.extractUsername(token));
		assertEquals(15L, jwtUtils.extractUserId(token));
		assertEquals("ROLE_USER", jwtUtils.extractRole(token));
	}

	@Test
	void shouldValidateTokenForMatchingUser() {
		String token = jwtUtils.generateToken(20L, "admin", "ROLE_ADMIN");
		UserDetails userDetails = User.builder()
			.username("admin")
			.password("encoded")
			.authorities("ROLE_ADMIN")
			.build();

		assertTrue(jwtUtils.isTokenValid(token, userDetails));
	}

	@Test
	void shouldInvalidateTokenForDifferentUser() {
		String token = jwtUtils.generateToken(20L, "admin", "ROLE_ADMIN");
		UserDetails userDetails = User.builder()
			.username("martin")
			.password("encoded")
			.authorities("ROLE_USER")
			.build();

		assertFalse(jwtUtils.isTokenValid(token, userDetails));
	}
}
