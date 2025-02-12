package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.dto.EditPrivacyRequest;
import com.duyhung.lydinc_backend.model.dto.NewCourseRequest;
import com.duyhung.lydinc_backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/new-course")
    public ResponseEntity<?> createNewCourse(@RequestBody NewCourseRequest request) {
        return ResponseEntity.ok(courseService.createNewCourse(request.getTitle(), request.getModules(), request.getLecturerId()));
    }

    @PostMapping("/edit-privacy")
    public ResponseEntity<?> editPrivacy(@RequestBody EditPrivacyRequest request) {
        courseService.editCoursePrivacy(request.getPrivacy(), request.getCourseId(), request.getUniversityIds(), request.getDeleteUniversityIds());
        return ResponseEntity.ok("Privacy settings updated successfully.");
    }

    @GetMapping("/courses-by-lecturer")
    public ResponseEntity<?> getCourseByLecturer(@RequestParam String lecturerId) {
        return ResponseEntity.ok(courseService.getCourseByLecturer(lecturerId));
    }

    @GetMapping("/courses-by-student")
    public ResponseEntity<?> getCourseByStudent(@RequestParam String studentId) {
        return ResponseEntity.ok(courseService.getCourseByStudent(studentId));
    }

    @GetMapping("/courses-privacy")
    public ResponseEntity<?> getCoursePrivacy(@RequestParam Integer courseId) {
        return ResponseEntity.ok(courseService.getCoursePrivacy(courseId));
    }

}
