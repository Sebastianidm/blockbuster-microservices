package com.blockbuster.users.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

	private Long id;

	private String username;

	private String email;

	private LocalDateTime createdAt;

	private RoleResponseDTO role;
}
