package com.blockbuster.transactions.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import feign.RequestInterceptor;
import feign.hc5.ApacheHttp5Client;

@Configuration
public class FeignInternalApiKeyConfig {

    @Bean
    public RequestInterceptor internalApiKeyRequestInterceptor(@Value("${internal.api.key}") String internalApiKey) {
        return template -> template.header("X-Internal-Api-Key", internalApiKey);
    }

    @Bean
    public CloseableHttpClient feignHttpClient() {
        return HttpClients.custom().build();
    }

    @Bean
    public Client feignClient(CloseableHttpClient feignHttpClient) {
        return new ApacheHttp5Client(feignHttpClient);
    }
}
