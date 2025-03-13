package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.auth.RegisterRequest;
import com.duyhung.lydinc_backend.service.AuthService;
import com.duyhung.lydinc_backend.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("admin")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(
            @RequestBody List<RegisterRequest> registerRequests
    ) throws MessagingException {
        return ResponseEntity.ok(userService.createStudentAccounts(registerRequests));
    }

    @GetMapping("/get-all-students")
    public ResponseEntity<?> getAllAccounts(
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) Integer universityId,
            @RequestParam Integer orderBy,
            @RequestParam int pageNo,
            @RequestParam int pageSize
    ) {
        return ResponseEntity.ok(userService.getAllAccounts(searchValue, universityId, orderBy, pageNo, pageSize));
    }

    @GetMapping("/get-all-lecturers")
    public ResponseEntity<?> getAllLecturers() {
        return ResponseEntity.ok(userService.getAllLecturers());
    }

    @PostMapping("create-new-lecturer")
    public ResponseEntity<?> createNewLecturer(
            @RequestBody RegisterRequest request
    ) {
        userService.createNewLecturer(
                request.getUsername(),
                request.getName(),
                request.getEmail(),
                request.getPhone()
        );
        return ResponseEntity.ok("Create account and send email successfully!");
    }


}
