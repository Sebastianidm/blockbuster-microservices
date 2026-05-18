package com.blockbuster.users.service;

import com.blockbuster.users.model.dto.LoginRequestDTO;
import com.blockbuster.users.model.dto.LoginResponseDTO;
import com.blockbuster.users.model.dto.RegisterUserRequestDTO;
import com.blockbuster.users.model.dto.UserResponseDTO;

public interface AuthService {

	UserResponseDTO register(RegisterUserRequestDTO request);

	LoginResponseDTO login(LoginRequestDTO request);
}
