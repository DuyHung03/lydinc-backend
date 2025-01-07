package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.Enrollment;
import com.duyhung.lydinc_backend.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/assign")
    public ResponseEntity<?> assignUsersToCourse(
            @RequestParam Integer courseId,
            @RequestBody List<String> userIds) {
        return ResponseEntity.ok(enrollmentService.assignUsersToCourse(userIds, courseId));
    }

    @GetMapping("/getEnrollment")
    public ResponseEntity<?> getEnrollment(@RequestParam Integer id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

}
