package com.blockbuster.users.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "user-controller", description = "Endpoints de consulta de usuarios")
public class UserController {

	private final UserService userService;

	@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
	@GetMapping
	@Operation(summary = "Listar usuarios", description = "Obtiene el listado completo de usuarios registrados.")
	@ApiResponse(responseCode = "200", description = "Listado recuperado exitosamente")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
	@GetMapping("/{id}")
	@Operation(summary = "Buscar usuario por id", description = "Obtiene un usuario especifico por su identificador.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Usuario encontrado"),
		@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	})
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@GetMapping("/internal/{id}")
	@Operation(summary = "Buscar usuario interno por id", description = "Endpoint interno usado por otros microservicios para consultar un usuario.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Usuario encontrado"),
		@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	})
	public ResponseEntity<UserResponseDTO> getInternalUserById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}
}
