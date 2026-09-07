package com.orbit.notification.controller;

import com.orbit.notification.model.Notification;
import com.orbit.notification.service.NotificationDispatcher;
import com.orbit.notification.service.NotificationSenderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationDispatcher dispatcher;
    private final NotificationSenderService senderService;

    public NotificationController(NotificationDispatcher dispatcher, NotificationSenderService senderService) {
        this.dispatcher = dispatcher;
        this.senderService = senderService;
    }

    @PostMapping
    public Map<String, Object> enqueue(@RequestBody Map<String, Object> body) {
        Notification n = Notification.of(
                (int) body.getOrDefault("priority", 3),
                (String) body.getOrDefault("channel", "IN_APP"),
                (String) body.getOrDefault("recipient", "unknown"),
                (String) body.getOrDefault("message", "")
        );
        boolean accepted = dispatcher.enqueue(n);
        return Map.of("accepted", accepted, "eventId", n.eventId(), "queueSize", dispatcher.size());
    }

    @GetMapping("/sent")
    public List<Notification> sent() {
        return senderService.getSentLog();
    }

    @GetMapping("/dead-letter")
    public List<Notification> deadLetter() {
        return senderService.getDeadLetterQueue();
    }
}
