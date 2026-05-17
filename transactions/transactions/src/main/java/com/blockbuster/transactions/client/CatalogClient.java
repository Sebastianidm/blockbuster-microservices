package com.blockbuster.transactions.client;

import com.blockbuster.transactions.client.dto.MovieClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-catalog", url = "http://localhost:8081/api/v1")
public interface CatalogClient {

    @GetMapping("/movies/{id}")
    MovieClientDTO getMovieById(@PathVariable("id") Long id);
}