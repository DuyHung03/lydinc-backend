package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/school")
public class SchoolController {
    private final SchoolService schoolService;

    @PostMapping("/create-new-school")
    public ResponseEntity<?> createNewSchool(
            @RequestParam String schoolName
    ) {
        return ResponseEntity.ok(schoolService.createNewSchool(schoolName));
    }

    @GetMapping("/get-all-schools")
    public ResponseEntity<?> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @GetMapping("/get-students-by-school")
    public ResponseEntity<?> getStudentBySchool(
            @RequestParam Integer schoolId
    ) {
        return ResponseEntity.ok(schoolService.getStudentsOfSchool(schoolId));
    }

}
