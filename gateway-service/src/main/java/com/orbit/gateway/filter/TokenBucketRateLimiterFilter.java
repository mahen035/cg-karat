package com.orbit.gateway.filter;

import com.orbit.gateway.demo.DemoModeState;
import com.orbit.gateway.ratelimit.TokenBucket;
import com.orbit.gateway.ratelimit.TokenBucketRegistry;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenBucketRateLimiterFilter implements GlobalFilter, Ordered {

    private final TokenBucketRegistry registry;
    private final DemoModeState demoModeState;

    public TokenBucketRateLimiterFilter(TokenBucketRegistry registry, DemoModeState demoModeState) {
        this.registry = registry;
        this.demoModeState = demoModeState;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!demoModeState.isRateLimitEnabled()) {
            return chain.filter(exchange); // NAIVE mode: no limiting at all
        }

        String clientKey = resolveClientKey(exchange);
        TokenBucket bucket = registry.getBucket(clientKey);

        if (bucket.tryConsume()) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
        return exchange.getResponse().setComplete();
    }

    private String resolveClientKey(ServerWebExchange exchange) {
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-Api-Key");
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey;
        }
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "anonymous";
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
