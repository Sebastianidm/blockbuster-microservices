package com.blockbuster.catalog.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeignInternalApiKeyConfigTest {

    private final FeignInternalApiKeyConfig config = new FeignInternalApiKeyConfig();

    @Test
    void shouldAttachInternalApiKeyHeader() {
        RequestInterceptor interceptor = config.internalApiKeyRequestInterceptor("catalog-internal-key");
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertEquals("catalog-internal-key",
                template.headers().get("X-Internal-Api-Key").iterator().next());
    }
}
