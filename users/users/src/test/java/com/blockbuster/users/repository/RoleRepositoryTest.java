package com.blockbuster.users.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RoleRepositoryTest {

	@Autowired
	private RoleRepository roleRepository;

	@Test
	void shouldFindSeededRoleByName() {
		assertTrue(roleRepository.findByName("ROLE_ADMIN").isPresent());
		assertEquals("ROLE_ADMIN", roleRepository.findByName("ROLE_ADMIN").orElseThrow().getName());
	}

	@Test
	void shouldConfirmRoleExistsByName() {
		assertTrue(roleRepository.existsByName("ROLE_USER"));
	}
}
