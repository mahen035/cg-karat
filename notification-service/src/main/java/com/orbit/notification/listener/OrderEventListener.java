package com.orbit.notification.listener;

import com.orbit.notification.model.Notification;
import com.orbit.notification.service.NotificationDispatcher;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for the "order-events" topic, activated only with the
 * "kafka" Spring profile (run: --spring.profiles.active=kafka) once you've
 * started the optional docker-compose Kafka broker. Without this profile,
 * use the REST endpoint (NotificationController) to enqueue notifications
 * directly for classroom demos - no Kafka required.
 */
@Component
@Profile("kafka")
public class OrderEventListener {

    private final NotificationDispatcher dispatcher;

    public OrderEventListener(NotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void onOrderEvent(String eventPayload) {
        // In a real implementation, deserialize eventPayload (JSON) into an
        // OrderEvent DTO and map its type to a priority, e.g.:
        //   PAYMENT_FAILED -> priority 1
        //   ORDER_SHIPPED  -> priority 3
        //   PROMO_OFFER    -> priority 5
        Notification n = Notification.of(3, "IN_APP", "customer", eventPayload);
        dispatcher.enqueue(n);
    }
}
