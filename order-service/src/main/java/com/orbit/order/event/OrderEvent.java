package com.orbit.order.event;

/**
 * Event published to Kafka topic "order-events" (or logged, if the "kafka"
 * profile isn't active). The Notification Service, Inventory Service, and
 * an Analytics consumer would all subscribe to this same topic
 * independently - this fan-out is why Orbit uses an event bus instead of
 * Order Service calling each of them synchronously (Day 24, Case Study 2
 * & 3).
 */
public record OrderEvent(String type, Long orderId, Long productId, String status) {
    public static OrderEvent orderConfirmed(Long orderId, Long productId) {
        return new OrderEvent("ORDER_CONFIRMED", orderId, productId, "CONFIRMED");
    }
    public static OrderEvent paymentFailed(Long orderId, Long productId) {
        return new OrderEvent("PAYMENT_FAILED", orderId, productId, "PAYMENT_FAILED");
    }
}
