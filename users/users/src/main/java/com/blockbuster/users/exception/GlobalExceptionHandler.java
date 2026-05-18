package com.blockbuster.users.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UsersException.class)
	public ResponseEntity<ApiErrorResponse> handleUsersException(UsersException exception, HttpServletRequest request) {
		return buildErrorResponse(exception.getStatus(), exception.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(
		MethodArgumentNotValidException exception,
		HttpServletRequest request
	) {
		String message = exception.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(error -> error.getDefaultMessage() == null ? "Solicitud invalida" : error.getDefaultMessage())
			.orElse("Solicitud invalida");

		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(
		BadCredentialsException exception,
		HttpServletRequest request
	) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciales invalidas", request.getRequestURI());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
		AccessDeniedException exception,
		HttpServletRequest request
	) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, "Acceso denegado", request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
		return buildErrorResponse(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"Se produjo un error interno en el servidor",
			request.getRequestURI()
		);
	}

	private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
		ApiErrorResponse errorResponse = ApiErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(status.value())
			.message(message)
			.path(path)
			.build();

		return ResponseEntity.status(status).body(errorResponse);
	}
}
