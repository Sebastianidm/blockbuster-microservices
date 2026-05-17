package com.blockbuster.users.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.model.entity.User;

class UserMapperTest {

	private final UserMapper userMapper = new UserMapper(new RoleMapper());

	@Test
	void shouldMapRegisterRequestToEntity() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("martin")
			.email("martin@blockbuster.com")
			.password("Admin123!")
			.build();
		Role role = Role.builder()
			.id(1L)
			.name("ROLE_USER")
			.build();

		User user = userMapper.toEntity(request, role, "encoded-password");

		assertEquals("martin", user.getUsername());
		assertEquals("martin@blockbuster.com", user.getEmail());
		assertEquals("encoded-password", user.getPassword());
		assertEquals(role, user.getRole());
	}

	@Test
	void shouldMapUserToResponse() {
		Role role = Role.builder()
			.id(2L)
			.name("ROLE_ADMIN")
			.build();
		User user = User.builder()
			.id(10L)
			.username("admin")
			.email("admin@blockbuster.com")
			.password("encoded-password")
			.createdAt(LocalDateTime.of(2026, 5, 17, 0, 0))
			.role(role)
			.build();

		UserResponseDTO response = userMapper.toResponse(user);

		assertEquals(10L, response.getId());
		assertEquals("admin", response.getUsername());
		assertEquals("admin@blockbuster.com", response.getEmail());
		assertEquals("ROLE_ADMIN", response.getRole().getName());
	}

	@Test
	void shouldReturnNullWhenUserIsNull() {
		assertNull(userMapper.toResponse(null));
	}
}
