package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Notification;
import com.duyhung.lydinc_backend.model.dto.NotificationDto;
import com.duyhung.lydinc_backend.repository.NotificationRepository;
import com.duyhung.lydinc_backend.websocket.NotificationWebSocketHandler;
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
    private final NotificationWebSocketHandler notificationWebSocketHandler;

    /**
     * Sends a notification to all students in a university.
     *
     * @param universityId the ID of the university
     * @param message      the notification message
     */
    public void sendNotificationToUniversityStudents(Integer universityId, String message) {
        try {
            logger.info("Initiating notification process for universityId: {}", universityId);

            // Create a new notification entity
            Notification notification = new Notification();
            notification.setUniversityId(universityId);
            notification.setMessage(message);
            notification.setTitle("New Course Assigned");
            notification.setCreatedAt(LocalDateTime.now());

            // Save notification to the database
            notificationRepository.save(notification);
            logger.info("Notification saved successfully for universityId: {} - Message: {}", universityId, message);

            // Send the notification via WebSocket
            notificationWebSocketHandler.sendNotification(notification);
            logger.info("Notification sent via WebSocket for universityId: {}", universityId);

        } catch (Exception e) {
            logger.error("Failed to send notification to universityId {}: {}", universityId, e.getMessage(), e);
        }
    }

    /**
     * Sends a notification to a specific student.
     *
     * @param userId  the ID of the user receiving the notification
     * @param message the notification message
     */
    public void sendNotificationToCourseStudents(String userId, String message) {
        try {
            logger.info("Initiating notification process for userId: {}", userId);

            // Create a new notification entity
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(message);
            notification.setTitle("New Course Assigned");
            notification.setCreatedAt(LocalDateTime.now());

            // Save notification to the database
            notificationRepository.save(notification);
            logger.info("Notification saved successfully for user: {} - Message: {}", userId, message);

            // Send the notification via WebSocket
            notificationWebSocketHandler.sendNotification(notification);
            logger.info("Notification sent via WebSocket for user: {}", userId);

        } catch (Exception e) {
            logger.error("Failed to send notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Retrieves all notifications for a given user.
     *
     * @param userId the ID of the user
     * @return a list of NotificationDto objects containing notification details
     */
    public List<NotificationDto> getAllNotifications(String userId) {
        try {
            logger.info("Fetching notifications for user: {}", userId);

            // Retrieve notifications from the database
            List<Notification> notifications = notificationRepository.findByUserId(userId);

            if (notifications.isEmpty()) {
                logger.info("No notifications found for user: {}", userId);
            }

            // Convert Notification entities to NotificationDto objects
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
            logger.error("Error retrieving notifications for user {}: {}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
