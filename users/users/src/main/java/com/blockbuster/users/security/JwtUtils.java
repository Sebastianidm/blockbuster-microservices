package com.blockbuster.users.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	private final SecretKey secretKey;
	private final long expiration;

	public JwtUtils(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.expiration}") long expiration
	) {
		this.secretKey = buildSigningKey(secret);
		this.expiration = expiration;
	}

	public String generateToken(Long userId, String username, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("role", role);

		return buildToken(claims, username);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Long extractUserId(String token) {
		return extractClaim(token, claims -> claims.get("userId", Long.class));
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);

		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = extractAllClaims(token);
		return resolver.apply(claims);
	}

	private String buildToken(Map<String, Object> claims, String username) {
		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(username)
			.setIssuedAt(now)
			.setExpiration(expirationDate)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	private SecretKey buildSigningKey(String secret) {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
