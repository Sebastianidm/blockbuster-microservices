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
@Schema(description = "Respuesta entregada luego de una autenticacion exitosa")
public class LoginResponseDTO {

	@Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
	private String token;

	@Schema(description = "Tipo de token", example = "Bearer")
	private String type;

	@Schema(description = "Identificador del usuario autenticado", example = "1")
	private Long userId;

	@Schema(description = "Username autenticado", example = "admin")
	private String username;

	@Schema(description = "Rol del usuario autenticado", example = "ROLE_ADMIN")
	private String role;
}
