package com.duyhung.lydinc_backend.websocket;

import com.duyhung.lydinc_backend.model.Notification;
import com.duyhung.lydinc_backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);

    // Stores active WebSocket sessions mapped by user ID or university ID
    private final ConcurrentMap<String, List<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    // ObjectMapper for serializing notifications (supports Java Time)
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // Service to handle JWT token parsing
    private final JwtService jwtService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extracts user ID or university ID from URI
        String path = extractPathFromUri(session.getUri());

        if (path != null) {
            // Adds session to the corresponding user/university's session list
            userSessions.computeIfAbsent(path, k -> new CopyOnWriteArrayList<>()).add(session);
            logger.info("WebSocket connected: path={} - Total sessions={}", path, userSessions.get(path).size());
        } else {
            logger.warn("WebSocket connection failed: Invalid user ID from URI {}", session.getUri());
            session.close(CloseStatus.BAD_DATA.withReason("Invalid user ID"));
        }
    }

    /**
     * Extracts the user ID or university ID from the WebSocket connection URI.
     * - If the URI contains "university", extracts the university ID.
     * - Otherwise, extracts and decodes the user ID from the JWT token.
     */
    private String extractPathFromUri(URI uri) {
        String path = uri.getPath();
        String[] segments = path.split("/");

        logger.info("Extracting path from URI: {}", uri);

        // Determine whether it's a university or user session
        String type = segments.length >= 4 ? segments[segments.length - 2] : null;
        if (Objects.equals(type, "university")) {
            logger.info("WebSocket connection for university: {}", segments[segments.length - 1]);
            return segments[segments.length - 1]; // University ID
        } else {
            String token = segments[segments.length - 1];
            Claims claims = jwtService.getClaimsFromToken(token);
            String userId = claims.get("id", String.class);
            logger.info("WebSocket connection for user: {}", userId);
            return userId; // User ID from token
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Extracts user/university ID from URI
        String path = extractPathFromUri(session.getUri());

        if (path != null) {
            // Removes session and cleans up empty session lists
            userSessions.computeIfPresent(path, (k, v) -> {
                v.remove(session);
                if (v.isEmpty()) {
                    logger.info("All WebSocket sessions closed for path={}", path);
                    return null; // Remove entry if no sessions left
                }
                logger.info("Session closed for path={}, remaining sessions={}", path, v.size());
                return v;
            });
        }
    }

    /**
     * Sends a notification message via WebSocket to the corresponding user or university.
     */
    public void sendNotification(Notification notification) {
        try {
            // Determine the recipient based on user ID or university ID
            String path = (notification.getUniversityId() != null)
                    ? notification.getUniversityId().toString()
                    : notification.getUserId();

            // Convert notification object to JSON
            String message = objectMapper.writeValueAsString(notification);

            // Retrieve active WebSocket sessions for the recipient
            List<WebSocketSession> sessions = userSessions.get(path);
            logger.info("Sending notification to path={} - Total sessions={}", path, (sessions != null ? sessions.size() : 0));

            if (sessions != null) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(message));
                        logger.info("Notification sent successfully to path={}", path);
                    } else {
                        logger.warn("WebSocket session closed for path={}", path);
                    }
                }
            } else {
                logger.warn("No active WebSocket sessions found for path={}", path);
            }
        } catch (Exception e) {
            logger.error("Error sending WebSocket notification. Notification: {}, Error: {}", notification, e.getMessage(), e);
        }
    }
}
