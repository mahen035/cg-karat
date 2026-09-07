package com.orbit.order.event;

public interface OrderEventPublisher {
    void publish(OrderEvent event);
}
