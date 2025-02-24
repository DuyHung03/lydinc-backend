package com.duyhung.lydinc_backend.service.kafka;


import com.duyhung.lydinc_backend.model.Notification;
import com.duyhung.lydinc_backend.websocket.NotificationWebSocketHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
public class KafkaConsumerService {
    private final NotificationWebSocketHandler webSocketHandler;

    public KafkaConsumerService(NotificationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void listen(Notification notification) {
        webSocketHandler.sendNotificationToClients(notification);
    }

}