package com.blockbuster.notifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Api de Notificaciones Blockbuster").version("1.0.0")
                .description("Microservicio encargado de las notificaciones de arriendos.")
                .contact(new Contact()
                .name("Sebastián Díaz")
                .email("sebast.diazm@duocuc.cl")));

    }

}
