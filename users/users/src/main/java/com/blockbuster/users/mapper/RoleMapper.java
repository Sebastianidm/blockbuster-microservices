package com.blockbuster.users.mapper;

import org.springframework.stereotype.Component;

import com.blockbuster.users.model.dto.RoleResponseDTO;
import com.blockbuster.users.model.entity.Role;

@Component
public class RoleMapper {

	public RoleResponseDTO toResponse(Role role) {
		if (role == null) {
			return null;
		}

		return RoleResponseDTO.builder()
			.id(role.getId())
			.name(role.getName())
			.build();
	}
}
