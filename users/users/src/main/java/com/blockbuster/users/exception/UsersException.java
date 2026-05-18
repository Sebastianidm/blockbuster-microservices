package com.blockbuster.users.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UsersException extends RuntimeException {

	private final HttpStatus status;

	public UsersException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
