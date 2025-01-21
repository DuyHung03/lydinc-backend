package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.NewCourseRequest;
import com.duyhung.lydinc_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/new-course")
    public ResponseEntity<?> createNewCourse(@RequestBody NewCourseRequest request) {
        return ResponseEntity.ok(courseService.createNewCourse(
                request.getTitle(),
                request.getLecturerId(),
                request.getUserIds()
        ));
    }


    @GetMapping("/courses-by-lecturer")
    public ResponseEntity<?> getCourseByLecturer(String lecturerId) {
        return ResponseEntity.ok(courseService.getCourseByLecturer(lecturerId));
    }

    @GetMapping("/courses-by-student")
    public ResponseEntity<?> getCourseByStudent(String studentId) {
        return ResponseEntity.ok(courseService.getCourseByStudent(studentId));
    }
    

}
