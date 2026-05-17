package com.blockbuster.users.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.model.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	void shouldFindSeededAdminByUsername() {
		assertTrue(userRepository.findByUsername("admin").isPresent());
		assertEquals("admin@blockbuster.com", userRepository.findByUsername("admin").orElseThrow().getEmail());
	}

	@Test
	void shouldValidateSeededUserUniquenessQueries() {
		assertTrue(userRepository.existsByUsername("admin"));
		assertTrue(userRepository.existsByEmail("admin@blockbuster.com"));
	}

	@Test
	void shouldPersistUserWithAssignedRole() {
		Role role = roleRepository.findByName("ROLE_USER").orElseThrow();
		User user = User.builder()
			.username("cliente01")
			.email("cliente01@blockbuster.com")
			.password("$2a$10$YUgU0/JqvvjF66JjKyUpEOTdygN8S./FDDhDHcUevBN3InHq5vl0y")
			.createdAt(LocalDateTime.now())
			.role(role)
			.build();

		User savedUser = userRepository.save(user);

		assertTrue(savedUser.getId() > 0);
		assertEquals("ROLE_USER", savedUser.getRole().getName());
	}
}
