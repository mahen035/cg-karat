package com.orbit.notification.service;

import com.orbit.notification.model.Notification;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Min-heap (java.util.PriorityQueue) based dispatcher.
 *
 * enqueue() is O(log n); dispatchNext() (poll) is O(log n) and always
 * returns the highest-urgency notification currently queued, regardless of
 * arrival order - this is the "fan-out" priority behavior discussed in
 * Day 24, Case Study 2.
 *
 * A simple in-memory Set doubles as the idempotency/dedup store, standing
 * in for a Redis SETNX-based check in production (Kafka gives at-least-once
 * delivery, so the same eventId could otherwise be processed twice).
 */
@Service
public class NotificationDispatcher {

    private final PriorityQueue<Notification> queue = new PriorityQueue<>();
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public synchronized boolean enqueue(Notification notification) {
        if (processedEventIds.contains(notification.eventId())) {
            return false; // duplicate event - drop it (idempotency)
        }
        processedEventIds.add(notification.eventId());
        queue.offer(notification);
        return true;
    }

    public synchronized Notification dispatchNext() {
        return queue.poll();
    }

    public synchronized int size() {
        return queue.size();
    }
}
