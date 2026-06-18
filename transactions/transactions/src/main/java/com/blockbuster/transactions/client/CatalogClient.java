package com.blockbuster.transactions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.blockbuster.transactions.client.dto.MovieClientResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "catalogClient", url = "${catalog.service.url}")
public interface CatalogClient {

	@PatchMapping("/api/v1/movies/{id}/stock/discount")
	@CircuitBreaker(name = "catalogClient", fallbackMethod = "checkAndDiscountStockFallback")
	MovieClientResponse checkAndDiscountStock(@PathVariable("id") Long movieId, @RequestParam int quantity);

	@PatchMapping("/api/v1/movies/{id}/stock/restore")
	@CircuitBreaker(name = "catalogClient", fallbackMethod = "restoreStockFallback")
	MovieClientResponse restoreStock(@PathVariable("id") Long movieId, @RequestParam int quantity);

	default MovieClientResponse checkAndDiscountStockFallback(Long movieId, int quantity, Throwable t) {
		throw new com.blockbuster.transactions.exception.TransactionException(
				org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
				"El servicio de catálogo no está disponible para verificar y descontar stock."
		);
	}

	default MovieClientResponse restoreStockFallback(Long movieId, int quantity, Throwable t) {
		throw new com.blockbuster.transactions.exception.TransactionException(
				org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
				"El servicio de catálogo no está disponible para restaurar stock."
		);
	}
}
