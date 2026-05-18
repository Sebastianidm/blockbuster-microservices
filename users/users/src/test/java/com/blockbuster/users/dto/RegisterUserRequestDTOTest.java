package com.blockbuster.users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blockbuster.users.model.dto.RegisterUserRequestDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RegisterUserRequestDTOTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldValidateRegisterRequestWhenDataIsValid() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("martin.caviedes")
			.email("martin@blockbuster.com")
			.password("Admin123!")
			.build();

		Set<ConstraintViolation<RegisterUserRequestDTO>> violations = validator.validate(request);

		assertTrue(violations.isEmpty());
	}

	@Test
	void shouldFailWhenEmailIsInvalidAndPasswordIsWeak() {
		RegisterUserRequestDTO request = RegisterUserRequestDTO.builder()
			.username("martin")
			.email("martin-at-blockbuster")
			.password("weakpass")
			.build();

		Set<String> messages = validator.validate(request)
			.stream()
			.map(ConstraintViolation::getMessage)
			.collect(Collectors.toSet());

		assertEquals(2, messages.size());
		assertTrue(messages.contains("El email debe tener un formato valido"));
		assertTrue(messages.contains("La password debe incluir mayuscula, minuscula, numero y caracter especial"));
	}
}
