package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.auth.RegisterRequest;
import com.duyhung.lydinc_backend.service.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("admin")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AuthService authService;

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(
            @RequestBody List<RegisterRequest> registerRequests
    ) throws MessagingException {
        return ResponseEntity.ok(authService.createAccount(registerRequests));
    }

}
