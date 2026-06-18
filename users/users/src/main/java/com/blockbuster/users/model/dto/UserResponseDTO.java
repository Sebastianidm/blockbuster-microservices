package com.blockbuster.users.model.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representacion de un usuario expuesto por la API")
public class UserResponseDTO {

	@Schema(description = "Identificador del usuario", example = "6")
	private Long id;

	@Schema(description = "Nombre de usuario", example = "martin.demo")
	private String username;

	@Schema(description = "Correo electronico del usuario", example = "martin.demo@duocuc.cl")
	private String email;

	@Schema(description = "Fecha de creacion de la cuenta", example = "2026-06-17T12:30:00")
	private LocalDateTime createdAt;

	@Schema(description = "Rol asignado al usuario")
	private RoleResponseDTO role;
}
