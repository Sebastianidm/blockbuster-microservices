package com.blockbuster.users.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.blockbuster.users.exception.GlobalExceptionHandler;
import com.blockbuster.users.exception.UserNotFoundException;
import com.blockbuster.users.model.dto.RoleResponseDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.security.JwtAuthenticationFilter;
import com.blockbuster.users.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Test
	void shouldReturnUsers() throws Exception {
		UserResponseDTO userResponse = UserResponseDTO.builder()
			.id(1L)
			.username("martin")
			.email("martin@blockbuster.com")
			.createdAt(LocalDateTime.of(2026, 5, 17, 1, 0))
			.role(RoleResponseDTO.builder().id(1L).name("ROLE_USER").build())
			.build();

		when(userService.getAllUsers()).thenReturn(List.of(userResponse));

		mockMvc.perform(get("/api/v1/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].username").value("martin"))
			.andExpect(jsonPath("$[0].role.name").value("ROLE_USER"));
	}

	@Test
	void shouldReturnUserById() throws Exception {
		UserResponseDTO userResponse = UserResponseDTO.builder()
			.id(5L)
			.username("cliente05")
			.email("cliente05@blockbuster.com")
			.createdAt(LocalDateTime.of(2026, 5, 17, 1, 0))
			.role(RoleResponseDTO.builder().id(2L).name("ROLE_EMPLOYEE").build())
			.build();

		when(userService.getUserById(5L)).thenReturn(userResponse);

		mockMvc.perform(get("/api/v1/users/5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(5))
			.andExpect(jsonPath("$.username").value("cliente05"));
	}

	@Test
	void shouldReturnInternalUserById() throws Exception {
		UserResponseDTO userResponse = UserResponseDTO.builder()
			.id(8L)
			.username("cliente08")
			.email("cliente08@blockbuster.com")
			.createdAt(LocalDateTime.of(2026, 5, 17, 1, 0))
			.role(RoleResponseDTO.builder().id(1L).name("ROLE_USER").build())
			.build();

		when(userService.getUserById(8L)).thenReturn(userResponse);

		mockMvc.perform(get("/api/v1/users/internal/8"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(8))
			.andExpect(jsonPath("$.email").value("cliente08@blockbuster.com"));
	}

	@Test
	void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
		when(userService.getUserById(99L))
			.thenThrow(new UserNotFoundException("No se encontro el usuario con id: 99"));

		mockMvc.perform(get("/api/v1/users/99"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("No se encontro el usuario con id: 99"));
	}
}
