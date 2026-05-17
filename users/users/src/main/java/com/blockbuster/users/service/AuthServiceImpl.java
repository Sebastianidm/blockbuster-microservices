package com.blockbuster.users.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.blockbuster.users.model.dto.LoginRequestDTO;
import com.blockbuster.users.model.dto.LoginResponseDTO;
import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.User;
import com.blockbuster.users.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;

	@Override
	public UserResponseDTO register(RegisterUserRequestDTO request) {
		return userService.registerUser(request);
	}

	@Override
	public LoginResponseDTO login(LoginRequestDTO request) {
		String normalizedUsername = request.getUsername().trim();

		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(normalizedUsername, request.getPassword())
		);

		User user = userService.findEntityByUsername(normalizedUsername);
		String role = user.getRole().getName();
		String token = jwtUtils.generateToken(user.getId(), user.getUsername(), role);

		return LoginResponseDTO.builder()
			.token(token)
			.type("Bearer")
			.userId(user.getId())
			.username(user.getUsername())
			.role(role)
			.build();
	}
}
