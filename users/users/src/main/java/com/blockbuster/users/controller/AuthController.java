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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
		return ResponseEntity.ok(authService.login(request));
	}
}
