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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}
}
