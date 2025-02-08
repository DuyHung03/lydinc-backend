package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("module")
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping("get-modules")
    public ResponseEntity<?> getModulesByCourse(
            @RequestParam Integer courseId
    ) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId));
    }

}
