package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAccounts(@RequestParam String adminId, @RequestParam int pageNo, @RequestParam int pageSize) {
        return ResponseEntity.ok(userService.getAllAccounts(adminId, pageNo, pageSize));
    }
}
