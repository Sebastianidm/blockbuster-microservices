package com.blockbuster.transactions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.blockbuster.transactions.client.dto.UserClientResponse;

@FeignClient(name = "usersClient", url = "${users.service.url}")
public interface UsersClient {

	@GetMapping("/api/v1/users/internal/{id}")
	UserClientResponse getUserById(@PathVariable("id") Long userId);
}
