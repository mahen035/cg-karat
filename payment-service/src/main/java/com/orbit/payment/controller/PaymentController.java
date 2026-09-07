package com.orbit.payment.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulated payment gateway.
 *
 * POST /api/payments/charge  -> normally succeeds instantly
 * POST /api/payments/chaos/enable  -> makes every 2nd request time out / fail,
 *                                     so you can demonstrate Order Service's
 *                                     Resilience4j circuit breaker tripping
 *                                     open (Day 24, Case Study 3).
 * POST /api/payments/chaos/disable -> back to normal
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final AtomicBoolean chaosMode = new AtomicBoolean(false);
    private final AtomicInteger counter = new AtomicInteger(0);

    @PostMapping("/charge")
    public Map<String, Object> charge(@RequestBody Map<String, Object> request) throws InterruptedException {
        if (chaosMode.get() && counter.incrementAndGet() % 2 == 0) {
            Thread.sleep(6000); // simulate a slow/failing downstream payment provider
            throw new RuntimeException("Payment provider timeout (chaos mode)");
        }
        return Map.of(
                "transactionId", UUID.randomUUID().toString(),
                "status", "SUCCESS",
                "amount", request.getOrDefault("amount", 0)
        );
    }

    @PostMapping("/chaos/enable")
    public String enableChaos() {
        chaosMode.set(true);
        return "Chaos mode ENABLED - every 2nd payment will time out (~6s) then fail";
    }

    @PostMapping("/chaos/disable")
    public String disableChaos() {
        chaosMode.set(false);
        return "Chaos mode DISABLED";
    }
}
