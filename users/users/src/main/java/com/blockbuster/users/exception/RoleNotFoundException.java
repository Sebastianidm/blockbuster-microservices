package com.blockbuster.users.exception;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends UsersException {

	public RoleNotFoundException(String roleName) {
		super("No se encontro el rol: " + roleName, HttpStatus.NOT_FOUND);
	}
}
