package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.UserService;
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
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAccounts(@RequestParam String adminId, @RequestParam int pageNo, @RequestParam int pageSize) {
        return ResponseEntity.ok(userService.getAllAccounts(adminId, pageNo, pageSize));
    }

    @GetMapping("/get-all-student")
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam String userId,
            @RequestParam String newPassword
    ) throws MessagingException {
        return ResponseEntity.ok(userService.changePassword(userId, newPassword));
    }
}
