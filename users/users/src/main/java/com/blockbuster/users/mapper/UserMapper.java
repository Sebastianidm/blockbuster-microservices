package com.blockbuster.users.mapper;

import org.springframework.stereotype.Component;

import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.model.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final RoleMapper roleMapper;

	public User toEntity(RegisterUserRequestDTO request, Role role, String encodedPassword) {
		return User.builder()
			.username(request.getUsername())
			.email(request.getEmail())
			.password(encodedPassword)
			.role(role)
			.build();
	}

	public UserResponseDTO toResponse(User user) {
		if (user == null) {
			return null;
		}

		return UserResponseDTO.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.createdAt(user.getCreatedAt())
			.role(roleMapper.toResponse(user.getRole()))
			.build();
	}
}
