package com.orbit.order.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Default publisher (no "kafka" profile active) - just logs the event.
 * Lets the whole project run with zero external infra for classroom demos.
 * Order creation is NEVER blocked on this publish - checkout succeeds
 * regardless of whether the event bus is reachable, which is the key
 * decoupling point made in Case Study 2/3.
 */
@Component
@Profile("!kafka")
public class LoggingOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingOrderEventPublisher.class);

    @Override
    public void publish(OrderEvent event) {
        log.info("[order-events] (no Kafka profile active - logging only) {}", event);
    }
}
