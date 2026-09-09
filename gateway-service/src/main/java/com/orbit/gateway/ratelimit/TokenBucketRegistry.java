package com.orbit.gateway.ratelimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Holds one TokenBucket per client key.
 */
@Component
public class TokenBucketRegistry {

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Value("${orbit.ratelimit.capacity:20}")
    private int capacity;

    @Value("${orbit.ratelimit.refill-per-second:5}")
    private double refillRatePerSecond;

    public TokenBucket getBucket(String clientKey) {
        return buckets.computeIfAbsent(clientKey,
                key -> new TokenBucket(capacity, refillRatePerSecond));
    }
}
