package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Notification;
import com.duyhung.lydinc_backend.model.dto.NotificationDto;
import com.duyhung.lydinc_backend.repository.NotificationRepository;
import com.duyhung.lydinc_backend.service.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final KafkaProducerService kafkaProducerService;

    public void sendNotificationToUniversityStudents(Integer universityId, String message) {
        try {
            logger.info("Sending notification to user: {}", universityId);

            Notification notification = new Notification();
            notification.setUniversityId(universityId);
            notification.setMessage(message);
            notification.setTitle("New Course Assigned");
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
            logger.info("Notification saved for universityId: {} - Message: {}", universityId, message);

            // Gửi qua Kafka
            kafkaProducerService.sendNotification(notification);
            logger.info("Notification sent via Kafka for universityId: {}", universityId);

        } catch (Exception e) {
            logger.error("Error sending notification to universityId {}: {}", universityId, e.getMessage(), e);
        }
    }

    public void sendNotificationToCourseStudents(String userId, String message) {
        try {
            logger.info("Sending notification to user: {}", userId);

            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(message);
            notification.setTitle("New Course Assigned");
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
            logger.info("Notification saved for user: {} - Message: {}", userId, message);

            // Gửi qua Kafka
            kafkaProducerService.sendNotification(notification);
            logger.info("Notification sent via Kafka for user: {}", userId);

        } catch (Exception e) {
            logger.error("Error sending notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    public List<NotificationDto> getAllNotifications(String userId) {
        try {
            logger.info("Fetching notifications for user: {}", userId);

            List<Notification> notifications = notificationRepository.findByUserId(userId);
            if (notifications.isEmpty()) {
                logger.info("No notifications found for user: {}", userId);
            }

            return notifications.stream().map(notification -> {
                logger.debug("Mapping notification: id={}, title={}, seen={}",
                        notification.getId(), notification.getTitle(), notification.isSeen());
                return NotificationDto.builder()
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .isSeen(notification.isSeen())
                        .type(notification.getType())
                        .createAt(notification.getCreatedAt())
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching notifications for user {}: {}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
