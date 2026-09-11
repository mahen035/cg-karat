package com.orbit.order.event;

public record OrderEvent(String type, Long orderId, Long productId, String status) {
    public static OrderEvent orderConfirmed(Long orderId, Long productId) {
        return new OrderEvent("ORDER_CONFIRMED", orderId, productId, "CONFIRMED");
    }
    public static OrderEvent paymentFailed(Long orderId, Long productId) {
        return new OrderEvent("PAYMENT_FAILED", orderId, productId, "PAYMENT_FAILED");
    }
}
