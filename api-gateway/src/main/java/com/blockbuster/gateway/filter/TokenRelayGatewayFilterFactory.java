package com.blockbuster.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class TokenRelayGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenRelayGatewayFilterFactory.Config> {

    public TokenRelayGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null) {
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("Authorization", authHeader)
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // No configuration properties needed for now
    }
}
