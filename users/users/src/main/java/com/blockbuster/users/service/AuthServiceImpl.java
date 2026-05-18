package com.blockbuster.users.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.blockbuster.users.client.NotificationsClient;
import com.blockbuster.users.client.dto.NotificationRequest;
import com.blockbuster.users.model.dto.LoginRequestDTO;
import com.blockbuster.users.model.dto.LoginResponseDTO;
import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.User;
import com.blockbuster.users.security.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final NotificationsClient notificationsClient;

	@Override
	public UserResponseDTO register(RegisterUserRequestDTO request) {
		UserResponseDTO registeredUser = userService.registerUser(request);
		sendWelcomeNotification(registeredUser);
		return registeredUser;
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

	private void sendWelcomeNotification(UserResponseDTO user) {
		NotificationRequest notificationRequest = NotificationRequest.builder()
			.userId(user.getId())
			.recipientEmail(user.getEmail())
			.subject("Bienvenido a Blockbuster")
			.message("Tu cuenta ha sido creada con exito. Ya puedes iniciar sesion y arrendar peliculas.")
			.type("USER_REGISTRATION")
			.build();

		try {
			notificationsClient.sendNotification(notificationRequest);
		} catch (RuntimeException ex) {
			log.warn("No se pudo enviar la notificacion de bienvenida para el usuario {}", user.getId(), ex);
		}
	}
}
