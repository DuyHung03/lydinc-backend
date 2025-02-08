package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("lesson")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("update-data")
    public ResponseEntity<?> updateLessonData(@RequestBody String data, @RequestParam String moduleId) {

        return ResponseEntity.ok(lessonService.updateLessonData(data, moduleId));

    }

    @GetMapping("get-data")
    public ResponseEntity<?> getLessonData(@RequestParam String moduleId) {

        return ResponseEntity.ok(lessonService.getLessonData(moduleId));

    }


}
