package com.orbit.gateway.ratelimit;

public class TokenBucket {

    private final int capacity;
    private final double refillRatePerSecond;
    private double tokens;
    private long lastRefillTimestampNanos;

    public TokenBucket(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillTimestampNanos = System.nanoTime();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double secondsElapsed = (now - lastRefillTimestampNanos) / 1_000_000_000.0;
        double tokensToAdd = secondsElapsed * refillRatePerSecond;
        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTimestampNanos = now;
        }
    }

    public synchronized double getAvailableTokens() {
        refill();
        return tokens;
    }
}
