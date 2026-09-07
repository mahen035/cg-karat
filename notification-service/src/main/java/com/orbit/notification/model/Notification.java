package com.orbit.notification.model;

import java.util.UUID;

/**
 * Day 24, Case Study 2: Notification System.
 *
 * priority: 1 = most urgent (e.g. "payment failed"), 5 = least urgent
 * (e.g. promotional). Comparable ordering feeds the min-heap dispatcher -
 * lower priority number is polled first; timestamp is the FIFO tiebreaker
 * within the same priority.
 */
public record Notification(
        String eventId,
        int priority,
        long timestamp,
        String channel,   // PUSH | EMAIL | SMS | IN_APP
        String recipient,
        String message
) implements Comparable<Notification> {

    public static Notification of(int priority, String channel, String recipient, String message) {
        return new Notification(UUID.randomUUID().toString(), priority, System.currentTimeMillis(),
                channel, recipient, message);
    }

    @Override
    public int compareTo(Notification other) {
        if (this.priority != other.priority) {
            return Integer.compare(this.priority, other.priority);
        }
        return Long.compare(this.timestamp, other.timestamp);
    }
}
