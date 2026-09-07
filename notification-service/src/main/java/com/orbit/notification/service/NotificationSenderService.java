package com.orbit.notification.service;

import com.orbit.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Polls the priority dispatcher on a fixed schedule and "sends" each
 * notification, simulating a provider call with retry + exponential
 * backoff on failure. In a real system, replace sendViaProvider() with a
 * call to FCM/APNs (push), SendGrid (email), or Twilio (SMS), and move the
 * dead-letter handling to an actual DLQ topic.
 */
@Service
public class NotificationSenderService {

    private static final Logger log = LoggerFactory.getLogger(NotificationSenderService.class);
    private static final int MAX_RETRIES = 3;

    private final NotificationDispatcher dispatcher;
    private final List<Notification> deadLetterQueue = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final List<Notification> sentLog = new java.util.concurrent.CopyOnWriteArrayList<>();

    public NotificationSenderService(NotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Scheduled(fixedDelay = 1000)
    public void drainQueue() {
        Notification next;
        while ((next = dispatcher.dispatchNext()) != null) {
            sendWithRetry(next, 0);
        }
    }

    private void sendWithRetry(Notification n, int attempt) {
        try {
            sendViaProvider(n);
            sentLog.add(n);
            log.info("Sent [{}] priority={} channel={} to={} : {}",
                    n.eventId(), n.priority(), n.channel(), n.recipient(), n.message());
        } catch (Exception e) {
            if (attempt < MAX_RETRIES) {
                log.warn("Send failed for {} (attempt {}), retrying...", n.eventId(), attempt + 1);
                sendWithRetry(n, attempt + 1);
            } else {
                log.error("Send permanently failed for {} after {} attempts - moving to DLQ",
                        n.eventId(), MAX_RETRIES);
                deadLetterQueue.add(n);
            }
        }
    }

    private void sendViaProvider(Notification n) {
        // Simulated provider call. Replace with real FCM/SendGrid/Twilio client.
    }

    public List<Notification> getSentLog() { return sentLog; }
    public List<Notification> getDeadLetterQueue() { return deadLetterQueue; }
}
