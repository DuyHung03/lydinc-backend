package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.UserService;
import com.duyhung.lydinc_backend.utils.SecurityUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping("/get-user-info")
    public ResponseEntity<?> getUserInfo(
    ) {
        String userId = SecurityUtils.getUserIdFromAuthentication();
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAccounts(@RequestParam int pageNo, @RequestParam int pageSize) {
        String userId = SecurityUtils.getUserIdFromAuthentication();
        return ResponseEntity.ok(userService.getAllAccounts(userId, pageNo, pageSize));
    }

    @GetMapping("/get-all-student")
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam String newPassword
    ) throws MessagingException {
        String userId = SecurityUtils.getUserIdFromAuthentication();
        return ResponseEntity.ok(userService.changePassword(userId, newPassword));
    }
}
