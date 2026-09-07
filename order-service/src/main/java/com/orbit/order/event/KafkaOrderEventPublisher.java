package com.orbit.order.event;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Active when run with --spring.profiles.active=kafka (requires the
 * optional docker-compose Kafka broker to be running).
 */
@Component
@Profile("kafka")
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaOrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OrderEvent event) {
        kafkaTemplate.send("order-events", event.orderId().toString(), event.toString());
    }
}
