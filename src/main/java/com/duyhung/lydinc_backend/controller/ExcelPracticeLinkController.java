package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.ExcelPracticeLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/practice-link")
public class ExcelPracticeLinkController {

    private final ExcelPracticeLinkService excelPracticeLinkService;

    @GetMapping("/get-practice-link")
    public ResponseEntity<?> getPracticeLink(
            @RequestParam String moduleIndex,
            @RequestParam String lessonIndex
    ) throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(excelPracticeLinkService.getPracticeLink(moduleIndex, lessonIndex));
    }

}
