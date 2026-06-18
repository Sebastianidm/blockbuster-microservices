package com.blockbuster.users.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Rol asociado a un usuario")
public class RoleResponseDTO {

	@Schema(description = "Identificador del rol", example = "1")
	private Long id;

	@Schema(description = "Nombre del rol", example = "ROLE_USER")
	private String name;
}
