package com.blockbuster.transactions.client;

import com.blockbuster.transactions.client.dto.UserClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "ms-users", url = "http://localhost:8082/api/v1")
public interface UserClient {

    @GetMapping("/users/{id}")
    UserClientDTO getUserById(@PathVariable("id") Long id);
}