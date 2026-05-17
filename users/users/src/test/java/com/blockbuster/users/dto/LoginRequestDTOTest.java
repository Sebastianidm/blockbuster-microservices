package com.blockbuster.users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blockbuster.users.model.dto.LoginRequestDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class LoginRequestDTOTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldValidateLoginRequestWhenDataIsValid() {
		LoginRequestDTO request = LoginRequestDTO.builder()
			.username("martin")
			.password("Admin123!")
			.build();

		Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(request);

		assertTrue(violations.isEmpty());
	}

	@Test
	void shouldFailWhenPasswordDoesNotMeetSecurityRules() {
		LoginRequestDTO request = LoginRequestDTO.builder()
			.username("martin")
			.password("password")
			.build();

		Set<String> messages = validator.validate(request)
			.stream()
			.map(ConstraintViolation::getMessage)
			.collect(Collectors.toSet());

		assertEquals(1, messages.size());
		assertTrue(messages.contains("La password debe incluir mayuscula, minuscula, numero y caracter especial"));
	}
}
