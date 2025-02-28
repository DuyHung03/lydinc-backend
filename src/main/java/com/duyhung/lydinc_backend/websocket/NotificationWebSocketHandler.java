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
    private final ConcurrentMap<String, List<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final JwtService jwtService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = extractPathFromUri(session.getUri());
        if (path != null) {
            userSessions.computeIfAbsent(path, k -> new CopyOnWriteArrayList<>()).add(session);
            logger.info("WebSocket connected: path={} - Total sessions={}", path, userSessions.get(path).size());
        } else {
            logger.warn("WebSocket connection failed: Invalid user ID from URI {}", session.getUri());
            session.close(CloseStatus.BAD_DATA.withReason("Invalid user ID"));
        }
    }

    private String extractPathFromUri(URI uri) {
        String path = uri.getPath();
        String[] segments = path.split("/");
        String type = segments.length >= 4 ? segments[segments.length - 2] : null;
        if (Objects.equals(type, "university")) {
            return segments[segments.length - 1];
        } else {
            String token = segments[segments.length - 1];
            Claims claims = jwtService.getClaimsFromToken(token);
            return claims.get("id", String.class);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String path = extractPathFromUri(session.getUri());
        if (path != null) {
            userSessions.computeIfPresent(path, (k, v) -> {
                v.remove(session);
                if (v.isEmpty()) {
                    logger.info("All sessions closed for path={}", path);
                    return null;
                }
                logger.info("Session closed for path={}, remaining sessions={}", path, v.size());
                return v;
            });
        }
    }

    public void sendNotification(Notification notification) {
        try {
            String path;
            if (notification.getUniversityId() != null) {
                path = notification.getUniversityId().toString();
            } else {
                path = notification.getUserId();
            }
            String message = objectMapper.writeValueAsString(notification);

            List<WebSocketSession> sessions = userSessions.get(path);
            logger.info("Sending notification to path={} - Total sessions={}", path, (sessions != null ? sessions.size() : 0));

            if (sessions != null) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(message));
                        logger.info("Notification sent to path={}", path);
                    } else {
                        logger.warn("WebSocket session closed for path={}", path);
                    }
                }
            } else {
                logger.warn("No active WebSocket sessions found for path={}", path);
            }
        } catch (Exception e) {
            logger.error("Error sending WebSocket notification to path={}", notification, e);
        }
    }
}
