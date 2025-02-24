package com.duyhung.lydinc_backend.service.kafka;

import com.duyhung.lydinc_backend.model.Notification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, Notification> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Notification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(Notification notification) {
        kafkaTemplate.send("notification-topic", notification);
    }
}
