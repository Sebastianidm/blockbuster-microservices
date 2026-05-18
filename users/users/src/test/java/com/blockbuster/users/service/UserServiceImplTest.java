package com.blockbuster.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blockbuster.users.exception.RoleNotFoundException;
import com.blockbuster.users.exception.UserAlreadyExistsException;
import com.blockbuster.users.exception.UserNotFoundException;
import com.blockbuster.users.mapper.RoleMapper;
import com.blockbuster.users.mapper.UserMapper;
import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.model.entity.User;
import com.blockbuster.users.repository.RoleRepository;
import com.blockbuster.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Captor
	private ArgumentCaptor<User> userCaptor;

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(
			userRepository,
			roleRepository,
			new UserMapper(new RoleMapper()),
			passwordEncoder
		);
	}

	@Test
	void shouldRegisterUserWhenUsernameAndEmailAreAvailable() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("  martin  ")
			.email("  Martin@Blockbuster.com  ")
			.password("Admin123!")
			.build();
		Role role = Role.builder()
			.id(1L)
			.name("ROLE_USER")
			.build();
		User savedUser = User.builder()
			.id(5L)
			.username("martin")
			.email("martin@blockbuster.com")
			.password("encoded-password")
			.createdAt(LocalDateTime.of(2026, 5, 17, 0, 0))
			.role(role)
			.build();

		when(userRepository.existsByUsername("martin")).thenReturn(false);
		when(userRepository.existsByEmail("martin@blockbuster.com")).thenReturn(false);
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
		when(passwordEncoder.encode("Admin123!")).thenReturn("encoded-password");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		UserResponseDTO response = userService.registerUser(request);

		verify(userRepository).save(userCaptor.capture());
		User persistedUser = userCaptor.getValue();

		assertEquals("martin", persistedUser.getUsername());
		assertEquals("martin@blockbuster.com", persistedUser.getEmail());
		assertEquals("encoded-password", persistedUser.getPassword());
		assertEquals(role, persistedUser.getRole());
		assertEquals("martin@blockbuster.com", response.getEmail());
		assertEquals("ROLE_USER", response.getRole().getName());
	}

	@Test
	void shouldThrowWhenUsernameAlreadyExists() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("martin")
			.email("martin@blockbuster.com")
			.password("Admin123!")
			.build();

		when(userRepository.existsByUsername("martin")).thenReturn(true);

		UserAlreadyExistsException exception = assertThrows(
			UserAlreadyExistsException.class,
			() -> userService.registerUser(request)
		);

		assertTrue(exception.getMessage().contains("username"));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void shouldThrowWhenEmailAlreadyExists() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("martin")
			.email("martin@blockbuster.com")
			.password("Admin123!")
			.build();

		when(userRepository.existsByUsername("martin")).thenReturn(false);
		when(userRepository.existsByEmail("martin@blockbuster.com")).thenReturn(true);

		UserAlreadyExistsException exception = assertThrows(
			UserAlreadyExistsException.class,
			() -> userService.registerUser(request)
		);

		assertTrue(exception.getMessage().contains("email"));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void shouldThrowWhenDefaultRoleDoesNotExist() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("martin")
			.email("martin@blockbuster.com")
			.password("Admin123!")
			.build();

		when(userRepository.existsByUsername("martin")).thenReturn(false);
		when(userRepository.existsByEmail("martin@blockbuster.com")).thenReturn(false);
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

		assertThrows(RoleNotFoundException.class, () -> userService.registerUser(request));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void shouldReturnAllUsersMappedToResponse() {
		Role role = Role.builder().id(1L).name("ROLE_USER").build();
		User user = User.builder()
			.id(10L)
			.username("cliente")
			.email("cliente@blockbuster.com")
			.createdAt(LocalDateTime.of(2026, 5, 17, 0, 0))
			.role(role)
			.build();

		when(userRepository.findAll()).thenReturn(List.of(user));

		List<UserResponseDTO> response = userService.getAllUsers();

		assertEquals(1, response.size());
		assertEquals("cliente", response.getFirst().getUsername());
	}

	@Test
	void shouldReturnUserById() {
		Role role = Role.builder().id(1L).name("ROLE_ADMIN").build();
		User user = User.builder()
			.id(20L)
			.username("admin")
			.email("admin@blockbuster.com")
			.createdAt(LocalDateTime.of(2026, 5, 17, 0, 0))
			.role(role)
			.build();

		when(userRepository.findById(20L)).thenReturn(Optional.of(user));

		UserResponseDTO response = userService.getUserById(20L);

		assertEquals(20L, response.getId());
		assertEquals("ROLE_ADMIN", response.getRole().getName());
	}

	@Test
	void shouldThrowWhenUserByIdDoesNotExist() {
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
	}

	@Test
	void shouldFindEntityByUsername() {
		User user = User.builder()
			.id(7L)
			.username("martin")
			.email("martin@blockbuster.com")
			.build();

		when(userRepository.findByUsername("martin")).thenReturn(Optional.of(user));

		User result = userService.findEntityByUsername("  martin  ");

		assertSame(user, result);
	}

	@Test
	void shouldThrowWhenEntityByUsernameDoesNotExist() {
		when(userRepository.findByUsername("martin")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.findEntityByUsername("martin"));
	}
}
