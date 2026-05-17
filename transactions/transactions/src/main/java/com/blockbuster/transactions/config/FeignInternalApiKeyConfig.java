package com.blockbuster.transactions.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignInternalApiKeyConfig {

    @Bean
    public RequestInterceptor internalApiKeyRequestInterceptor(@Value("${internal.api.key}") String internalApiKey) {
        return template -> template.header("X-Internal-Api-Key", internalApiKey);
    }
}
