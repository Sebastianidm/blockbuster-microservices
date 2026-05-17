package com.blockbuster.users.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends UsersException {

	public UserAlreadyExistsException(String field, String value) {
		super("Ya existe un usuario con " + field + ": " + value, HttpStatus.CONFLICT);
	}
}
