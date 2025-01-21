package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/university")
public class UniversityController {
    private final UniversityService universityService;

    @PostMapping("/create-new-university")
    public ResponseEntity<?> createNewUniversity(
            @RequestParam String shortName,
            @RequestParam String fullName,
            @RequestParam(required = false) String logo,
            @RequestParam(required = false) String location
    ) {
        return ResponseEntity.ok(universityService.createNewUniversity(
                shortName, fullName, logo, location));
    }

    @GetMapping("/get-all-universities")
    public ResponseEntity<?> getAllUniversities() {
        return ResponseEntity.ok(universityService.getAllUniversities());
    }

    @GetMapping("/get-students-by-university")
    public ResponseEntity<?> getStudentByUniversity(
            @RequestParam Integer universityId
    ) {
        return ResponseEntity.ok(universityService.getStudentsOfUniversity(universityId));
    }

}
