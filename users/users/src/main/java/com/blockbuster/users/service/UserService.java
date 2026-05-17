package com.blockbuster.users.service;

import java.util.List;

import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;
import com.blockbuster.users.model.entity.User;

public interface UserService {

	UserResponseDTO registerUser(RegisterUserRequestDTO request);

	List<UserResponseDTO> getAllUsers();

	UserResponseDTO getUserById(Long id);

	User findEntityByUsername(String username);
}
