package com.blockbuster.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blockbuster.users.model.dto.LoginRequestDTO;
import com.blockbuster.users.model.dto.LoginResponseDTO;
import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "auth-controller", description = "Endpoints de autenticacion y registro de usuarios")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario con rol de cliente.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
		@ApiResponse(responseCode = "400", description = "Datos invalidos o error de validacion"),
		@ApiResponse(responseCode = "409", description = "El username o email ya existe")
	})
	public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	@Operation(summary = "Iniciar sesion", description = "Autentica un usuario y retorna el token JWT para consumir endpoints protegidos.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Login exitoso"),
		@ApiResponse(responseCode = "400", description = "Credenciales invalidas o error de validacion"),
		@ApiResponse(responseCode = "401", description = "No autorizado")
	})
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
		return ResponseEntity.ok(authService.login(request));
	}
}
