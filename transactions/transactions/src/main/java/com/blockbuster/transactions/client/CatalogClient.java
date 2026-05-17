package com.blockbuster.transactions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.blockbuster.transactions.client.dto.MovieClientResponse;

@FeignClient(name = "catalogClient", url = "${catalog.service.url}")
public interface CatalogClient {

	@PatchMapping("/api/v1/movies/{id}/stock/discount")
	MovieClientResponse checkAndDiscountStock(@PathVariable("id") Long movieId, @RequestParam int quantity);

	@PatchMapping("/api/v1/movies/{id}/stock/restore")
	MovieClientResponse restoreStock(@PathVariable("id") Long movieId, @RequestParam int quantity);
}
