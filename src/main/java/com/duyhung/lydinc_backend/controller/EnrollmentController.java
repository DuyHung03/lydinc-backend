package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/getEnrollment")
    public ResponseEntity<?> getEnrollment(@RequestParam Integer id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }
}
