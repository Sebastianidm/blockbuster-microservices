package com.blockbuster.users.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SecurityTestController {

	@GetMapping("/api/v1/auth/ping")
	public String publicPing() {
		return "public";
	}

	@GetMapping("/api/v1/users/secure-ping")
	public String securePing() {
		return "secured";
	}

	@GetMapping("/api/v1/users/internal/ping")
	public String internalPing() {
		return "internal";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/api/v1/users/admin-ping")
	public String adminPing() {
		return "admin";
	}
}
