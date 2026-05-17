package com.blockbuster.users.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.blockbuster.users.exception.UserNotFoundException;
import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.model.entity.User;
import com.blockbuster.users.service.UserService;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void shouldLoadUserDetailsWithRoleAsAuthority() {
		User user = User.builder()
			.username("martin")
			.password("encoded-password")
			.role(Role.builder().name("ROLE_ADMIN").build())
			.build();

		when(userService.findEntityByUsername("martin")).thenReturn(user);

		UserDetails userDetails = customUserDetailsService.loadUserByUsername("martin");

		assertEquals("martin", userDetails.getUsername());
		assertEquals("encoded-password", userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
	}

	@Test
	void shouldThrowUsernameNotFoundWhenUserDoesNotExist() {
		when(userService.findEntityByUsername("martin"))
			.thenThrow(new UserNotFoundException("No se encontro el usuario con username: martin"));

		assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("martin"));
	}
}
