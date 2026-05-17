package com.blockbuster.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.blockbuster.users.model.dto.LoginRequestDTO;
import com.blockbuster.users.model.dto.LoginResponseDTO;
import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.model.entity.User;
import com.blockbuster.users.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserService userService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtils jwtUtils;

	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	void shouldDelegateRegisterToUserService() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("martin")
			.email("martin@blockbuster.com")
			.password("Admin123!")
			.build();
		UserResponseDTO response = UserResponseDTO.builder()
			.id(1L)
			.username("martin")
			.email("martin@blockbuster.com")
			.build();

		when(userService.registerUser(request)).thenReturn(response);

		UserResponseDTO result = authService.register(request);

		assertSame(response, result);
	}

	@Test
	void shouldAuthenticateAndGenerateJwtOnLogin() {
		LoginRequestDTO request = LoginRequestDTO.builder()
			.username("  admin  ")
			.password("Admin123!")
			.build();
		User user = User.builder()
			.id(10L)
			.username("admin")
			.password("encoded-password")
			.role(Role.builder().name("ROLE_ADMIN").build())
			.build();

		when(userService.findEntityByUsername("admin")).thenReturn(user);
		when(jwtUtils.generateToken(10L, "admin", "ROLE_ADMIN")).thenReturn("jwt-token");

		LoginResponseDTO response = authService.login(request);

		verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("admin", "Admin123!"));
		assertEquals("jwt-token", response.getToken());
		assertEquals("Bearer", response.getType());
		assertEquals("ROLE_ADMIN", response.getRole());
	}
}
