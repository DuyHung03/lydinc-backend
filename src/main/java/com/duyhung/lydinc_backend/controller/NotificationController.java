package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.NotificationService;
import com.duyhung.lydinc_backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("get-by-user")
    public ResponseEntity<?> getNotifications() {
        String userId = SecurityUtils.getUserIdFromAuthentication();
        return ResponseEntity.ok(notificationService.getAllNotifications(userId));
    }

}
