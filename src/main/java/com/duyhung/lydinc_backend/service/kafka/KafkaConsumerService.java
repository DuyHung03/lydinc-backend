package com.duyhung.lydinc_backend.service.kafka;

import com.duyhung.lydinc_backend.model.Notification;
import com.duyhung.lydinc_backend.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final NotificationWebSocketHandler webSocketHandler;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void listen(Notification notification) {
        webSocketHandler.sendNotification(notification);
    }
}