package com.blockbuster.transactions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.blockbuster.transactions.client.dto.UserClientResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "usersClient", url = "${users.service.url}")
public interface UsersClient {

	@GetMapping("/api/v1/users/internal/{id}")
	@CircuitBreaker(name = "usersClient", fallbackMethod = "getUserByIdFallback")
	UserClientResponse getUserById(@PathVariable("id") Long userId);

	default UserClientResponse getUserByIdFallback(Long userId, Throwable t) {
		throw new com.blockbuster.transactions.exception.TransactionException(
				org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
				"El servicio de usuarios no está disponible para validar el usuario."
		);
	}
}
