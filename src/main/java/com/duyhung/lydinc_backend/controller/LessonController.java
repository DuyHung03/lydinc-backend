package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.dto.LessonDto;
import com.duyhung.lydinc_backend.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("lesson")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("update-data")
    public ResponseEntity<?> updateLessonData(
            @RequestBody List<LessonDto> lessons,
            @RequestParam String moduleId
    ) {
        return ResponseEntity.ok(lessonService.updateLessonData(lessons, moduleId));
    }

    @GetMapping("get-lesson-data")
    public ResponseEntity<?> getLessonData(
            @RequestParam String moduleId,
            Integer courseId
    ) {
        return ResponseEntity.ok(lessonService.getLessonData(moduleId, courseId));
    }
}
