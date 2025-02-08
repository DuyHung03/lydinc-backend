package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Module;
import com.duyhung.lydinc_backend.model.dto.ModuleDto;
import com.duyhung.lydinc_backend.repository.CourseRepository;
import com.duyhung.lydinc_backend.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public List<ModuleDto> getModulesByCourse(Integer courseId) {
        try {
            List<Module> modules = moduleRepository.findByCourse_CourseId(courseId);
            return modules.stream().map(module -> ModuleDto.builder()
                            .moduleId(module.getModuleId())
                            .moduleTitle(module.getModuleTitle())
                            .level(module.getLevel())
                            .index(module.getIndex())
                            .parentModuleId(module.getParentModuleId())
                            .status(module.getStatus())
                            .courseId(courseId)
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve modules. Please try again later.");
        }
    }
}
