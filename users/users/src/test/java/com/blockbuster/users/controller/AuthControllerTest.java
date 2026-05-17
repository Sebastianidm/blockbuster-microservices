package com.blockbuster.users.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import com.blockbuster.users.exception.GlobalExceptionHandler;
import com.blockbuster.users.model.dto.LoginResponseDTO;
import com.blockbuster.users.model.dto.RoleResponseDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.security.JwtAuthenticationFilter;
import com.blockbuster.users.service.AuthService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Test
	void shouldRegisterUser() throws Exception {
		UserResponseDTO response = UserResponseDTO.builder()
			.id(1L)
			.username("martin")
			.email("martin@blockbuster.com")
			.role(RoleResponseDTO.builder().id(1L).name("ROLE_USER").build())
			.build();

		when(authService.register(org.mockito.ArgumentMatchers.any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "martin",
					  "email": "martin@blockbuster.com",
					  "password": "Admin123!"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.username").value("martin"))
			.andExpect(jsonPath("$.role.name").value("ROLE_USER"));
	}

	@Test
	void shouldLoginAndReturnJwt() throws Exception {
		LoginResponseDTO response = LoginResponseDTO.builder()
			.token("jwt-token")
			.type("Bearer")
			.userId(10L)
			.username("admin")
			.role("ROLE_ADMIN")
			.build();

		when(authService.login(org.mockito.ArgumentMatchers.any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "admin",
					  "password": "Admin123!"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("jwt-token"))
			.andExpect(jsonPath("$.type").value("Bearer"))
			.andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
	}

	@Test
	void shouldReturnBadRequestWhenRegisterPayloadIsInvalid() throws Exception {
		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "",
					  "email": "bad-email",
					  "password": "weak"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.path").value("/api/v1/auth/register"));
	}

	@Test
	void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
		when(authService.login(org.mockito.ArgumentMatchers.any()))
			.thenThrow(new BadCredentialsException("Bad credentials"));

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "username": "admin",
					  "password": "BadPass123!"
					}
					"""))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("Credenciales invalidas"));
	}
}
