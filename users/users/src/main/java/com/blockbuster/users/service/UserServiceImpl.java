package com.blockbuster.users.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blockbuster.users.exception.RoleNotFoundException;
import com.blockbuster.users.exception.UserAlreadyExistsException;
import com.blockbuster.users.exception.UserNotFoundException;
import com.blockbuster.users.mapper.UserMapper;
import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.model.entity.User;
import com.blockbuster.users.repository.RoleRepository;
import com.blockbuster.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final String DEFAULT_ROLE = "ROLE_USER";

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserResponseDTO registerUser(RegisterUserRequestDTO request) {
		String normalizedUsername = normalizeUsername(request.getUsername());
		String normalizedEmail = normalizeEmail(request.getEmail());

		validateUniqueFields(normalizedUsername, normalizedEmail);

		Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
			.orElseThrow(() -> new RoleNotFoundException(DEFAULT_ROLE));
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		RegisterUserRequestDTO sanitizedRequest = RegisterUserRequestDTO.builder()
			.username(normalizedUsername)
			.email(normalizedEmail)
			.password(request.getPassword())
			.build();

		User userToSave = userMapper.toEntity(sanitizedRequest, defaultRole, encodedPassword);
		User savedUser = userRepository.save(userToSave);

		return userMapper.toResponse(savedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAll()
			.stream()
			.map(userMapper::toResponse)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDTO getUserById(Long id) {
		return userMapper.toResponse(findUserById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public User findEntityByUsername(String username) {
		String normalizedUsername = normalizeUsername(username);

		return userRepository.findByUsername(normalizedUsername)
			.orElseThrow(() -> new UserNotFoundException("No se encontro el usuario con username: " + normalizedUsername));
	}

	private User findUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("No se encontro el usuario con id: " + id));
	}

	private void validateUniqueFields(String username, String email) {
		if (userRepository.existsByUsername(username)) {
			throw new UserAlreadyExistsException("username", username);
		}

		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("email", email);
		}
	}

	private String normalizeUsername(String username) {
		return username == null ? null : username.trim();
	}

	private String normalizeEmail(String email) {
		return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
	}
}
