package com.blockbuster.users.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SecurityTestController.class)
@Import({
	SecurityConfig.class,
	SecurityBeansConfig.class,
	JwtUtils.class,
	JwtAuthenticationFilter.class,
	JwtAuthenticationEntryPoint.class,
	JwtAccessDeniedHandler.class,
	InternalApiKeyFilter.class
})
@ActiveProfiles("test")
@TestPropertySource(properties = "internal.api.key=test-internal-key")
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserDetailsService userDetailsService;

	@Test
	void shouldAllowPublicAuthEndpointWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/v1/auth/ping").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("public"));
	}

	@Test
	void shouldRejectProtectedEndpointWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/v1/users/secure-ping").with(csrf()))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("No autorizado"));
	}

	@Test
	void shouldRejectInternalUsersEndpointWithoutApiKey() throws Exception {
		mockMvc.perform(get("/api/v1/users/internal/ping").with(csrf()))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string("{\"message\":\"API key interna invalida\"}"));
	}

	@Test
	void shouldAllowInternalUsersEndpointWithApiKey() throws Exception {
		mockMvc.perform(get("/api/v1/users/internal/ping")
				.header("X-Internal-Api-Key", "test-internal-key")
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("internal"));
	}

	@Test
	void shouldAllowProtectedEndpointWithAuthenticatedUser() throws Exception {
		mockMvc.perform(get("/api/v1/users/secure-ping")
				.with(user("martin").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("secured"));
	}

	@Test
	void shouldDenyAdminEndpointWhenRoleIsInsufficient() throws Exception {
		mockMvc.perform(get("/api/v1/users/admin-ping")
				.with(user("martin").roles("USER"))
				.with(csrf()))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message").value("Acceso denegado"));
	}

	@Test
	void shouldAllowAdminEndpointWhenRoleIsAdmin() throws Exception {
		mockMvc.perform(get("/api/v1/users/admin-ping")
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("admin"));
	}
}
