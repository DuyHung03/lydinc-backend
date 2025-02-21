package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/send-notification")
    public void sendNotificationToUser(Notification notification) {
        // Send a message to a specific user
        simpMessagingTemplate.convertAndSendToUser(
                notification.getUserId(), // Send to this user
                "/queue/notifications", // The destination
                notification // The message content
        );
    }

}
