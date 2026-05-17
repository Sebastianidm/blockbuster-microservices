package com.blockbuster.catalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blockbuster Catalog API")
                        .version("v1")
                        .description("API REST para la gestión del catálogo de películas y categorías")
                        .contact(new Contact()
                                .name("Martin Caviedes")));
    }
}
