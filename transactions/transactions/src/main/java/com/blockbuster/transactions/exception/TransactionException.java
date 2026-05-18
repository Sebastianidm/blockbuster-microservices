package com.blockbuster.transactions.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class TransactionException extends RuntimeException {

	private final HttpStatus status;

	public TransactionException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
