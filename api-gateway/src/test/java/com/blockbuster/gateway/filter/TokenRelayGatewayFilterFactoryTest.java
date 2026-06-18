package com.blockbuster.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class TokenRelayGatewayFilterFactoryTest {

    private final TokenRelayGatewayFilterFactory factory = new TokenRelayGatewayFilterFactory();

    @Test
    void shouldRelayAuthorizationHeaderWhenPresent() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/movies")
                .header("Authorization", "Bearer test-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        AtomicReference<String> authorizationHeader = new AtomicReference<>();

        factory.apply(new TokenRelayGatewayFilterFactory.Config())
                .filter(exchange, currentExchange -> captureAuthorization(currentExchange, authorizationHeader))
                .block();

        assertThat(authorizationHeader.get()).isEqualTo("Bearer test-token");
    }

    @Test
    void shouldContinueWithoutAuthorizationHeaderWhenAbsent() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/movies").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        AtomicReference<String> authorizationHeader = new AtomicReference<>();

        factory.apply(new TokenRelayGatewayFilterFactory.Config())
                .filter(exchange, currentExchange -> captureAuthorization(currentExchange, authorizationHeader))
                .block();

        assertThat(authorizationHeader.get()).isNull();
    }

    private Mono<Void> captureAuthorization(ServerWebExchange exchange, AtomicReference<String> target) {
        target.set(exchange.getRequest().getHeaders().getFirst("Authorization"));
        return Mono.empty();
    }
}
