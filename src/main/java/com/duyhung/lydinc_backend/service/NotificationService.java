package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Notification;
import com.duyhung.lydinc_backend.repository.CourseRepository;
import com.duyhung.lydinc_backend.repository.NotificationRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import com.duyhung.lydinc_backend.service.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    private final CourseRepository courseRepository;

    public void sendNotificationToCourseStudents(String userId, String message) {
        Notification notification = Notification.builder()
                .title("New new")
                .message(message)
                .userId(userId)
//                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        kafkaProducerService.sendNotification(notification);
    }
}