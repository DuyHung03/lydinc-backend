package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.auth.LoginRequest;
import com.duyhung.lydinc_backend.model.auth.RegisterRequest;
import com.duyhung.lydinc_backend.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    public final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> signIn(
            @RequestBody RegisterRequest registerRequest
    ) throws Exception {
        return ResponseEntity.ok(authService.signIn(registerRequest));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest authRequest,
            HttpServletResponse response
    ) throws Exception {
        return ResponseEntity.ok(authService.login(authRequest, response));
    }
}
