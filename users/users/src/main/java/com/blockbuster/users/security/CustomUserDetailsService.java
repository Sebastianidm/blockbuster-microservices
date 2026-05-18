package com.blockbuster.users.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.blockbuster.users.exception.UserNotFoundException;
import com.blockbuster.users.model.entity.Role;
import com.blockbuster.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			com.blockbuster.users.model.entity.User user = userService.findEntityByUsername(username);
			Role role = user.getRole();

			return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(new SimpleGrantedAuthority(role.getName()))
				.build();
		} catch (UserNotFoundException exception) {
			throw new UsernameNotFoundException(exception.getMessage(), exception);
		}
	}
}
