package com.blockbuster.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI usersOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("Blockbuster MS-Users API")
				.version("v1")
				.description("Microservicio de seguridad y gestion de usuarios para Blockbuster"));
	}
}
