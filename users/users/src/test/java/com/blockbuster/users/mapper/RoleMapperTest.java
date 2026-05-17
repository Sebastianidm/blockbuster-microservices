package com.blockbuster.users.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.blockbuster.users.model.dto.RoleResponseDTO;
import com.blockbuster.users.model.entity.Role;

class RoleMapperTest {

	private final RoleMapper roleMapper = new RoleMapper();

	@Test
	void shouldMapRoleToResponse() {
		Role role = Role.builder()
			.id(1L)
			.name("ROLE_ADMIN")
			.build();

		RoleResponseDTO response = roleMapper.toResponse(role);

		assertEquals(1L, response.getId());
		assertEquals("ROLE_ADMIN", response.getName());
	}

	@Test
	void shouldReturnNullWhenRoleIsNull() {
		assertNull(roleMapper.toResponse(null));
	}
}
