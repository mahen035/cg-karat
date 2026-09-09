package com.orbit.gateway.demo;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DemoModeState {
    private final AtomicBoolean rateLimitEnabled = new AtomicBoolean(true);

    public boolean isRateLimitEnabled() {
        return rateLimitEnabled.get();
    }

    public void setRateLimitEnabled(boolean enabled) {
        rateLimitEnabled.set(enabled);
    }
}
