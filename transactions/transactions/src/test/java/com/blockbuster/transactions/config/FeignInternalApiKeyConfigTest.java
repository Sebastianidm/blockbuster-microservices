package com.blockbuster.transactions.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

import feign.Client;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.hc5.ApacheHttp5Client;

class FeignInternalApiKeyConfigTest {

    private final FeignInternalApiKeyConfig config = new FeignInternalApiKeyConfig();

    @Test
    void shouldAttachInternalApiKeyHeader() {
        RequestInterceptor interceptor = config.internalApiKeyRequestInterceptor("test-internal-key");
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertEquals("test-internal-key", template.headers().get("X-Internal-Api-Key").iterator().next());
    }

    @Test
    void shouldCreateApacheHttp5FeignClient() {
        CloseableHttpClient httpClient = config.feignHttpClient();
        Client client = config.feignClient(httpClient);

        assertNotNull(httpClient);
        assertInstanceOf(ApacheHttp5Client.class, client);
    }
}
