package com.orbit.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Calls the (external, from Order Service's point of view) Payment Service.
 *
 * Wrapped with a Resilience4j circuit breaker: if Payment Service is slow
 * or failing, the breaker trips OPEN after the configured failure
 * threshold and subsequent calls fail fast (calling chargeFallback)
 * instead of piling up Order Service threads waiting on a dead
 * dependency. This is the exact "cascading failure" scenario discussed in
 * Day 24, Case Study 3's bottleneck-analysis Q&A.
 *
 * See resilience4j config in application.yml for threshold/wait-duration.
 */
@Component
public class PaymentClient {

    private final RestTemplate restTemplate;
    private final String paymentServiceUrl;

    public PaymentClient(RestTemplate restTemplate,
                          org.springframework.core.env.Environment env) {
        this.restTemplate = restTemplate;
        this.paymentServiceUrl = env.getProperty("orbit.payment-service.url", "http://localhost:8083");
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "chargeFallback")
    public Map<String, Object> charge(Long orderId, BigDecimal amount) {
        Map<String, Object> request = Map.of("orderId", orderId, "amount", amount);
        return restTemplate.postForObject(paymentServiceUrl + "/api/payments/charge", request, Map.class);
    }

    @SuppressWarnings("unused")
    private Map<String, Object> chargeFallback(Long orderId, BigDecimal amount, Throwable t) {
        return Map.of(
                "status", "FAILED_FAST",
                "reason", "Payment service unavailable (circuit breaker open): " + t.getMessage()
        );
    }
}
