package com.blockbuster.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blockbuster.users.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = "role")
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
