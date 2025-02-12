package com.duyhung.lydinc_backend.controller;

import com.duyhung.lydinc_backend.model.dto.UpdateModuleRequest;
import com.duyhung.lydinc_backend.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("update-course")
    public ResponseEntity<?> updateModuleByCourseId(
            @RequestBody UpdateModuleRequest request
    ) {
        return ResponseEntity.ok(moduleService.updateModulesTitle(request));
    }

}
