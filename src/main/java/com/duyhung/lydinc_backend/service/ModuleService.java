package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Course;
import com.duyhung.lydinc_backend.model.Module;
import com.duyhung.lydinc_backend.model.dto.ModuleDto;
import com.duyhung.lydinc_backend.model.dto.ModulesResponse;
import com.duyhung.lydinc_backend.model.dto.UpdateModuleRequest;
import com.duyhung.lydinc_backend.repository.CourseRepository;
import com.duyhung.lydinc_backend.repository.LessonRepository;
import com.duyhung.lydinc_backend.repository.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private static final Logger logger = LogManager.getLogger(ModuleService.class);
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    public ModulesResponse getModulesByCourse(Integer courseId) {
        try {
            logger.info("Fetching modules for course with ID: {}", courseId);

            List<Module> modules = moduleRepository.findByCourse_CourseId(courseId);
            Course course = courseRepository.findById(courseId).orElseThrow(() -> {
                logger.error("Course not found for ID: {}", courseId);
                return new RuntimeException("Course not found");
            });

            List<ModuleDto> moduleDtos = modules.stream().map(module -> ModuleDto.builder().moduleId(module.getModuleId()).moduleTitle(module.getModuleTitle()).level(module.getLevel()).index(module.getIndex()).parentModuleId(module.getParentModuleId()).status(module.getStatus()).courseId(courseId).build()).toList();

            logger.info("Successfully fetched {} modules for course ID: {}", moduleDtos.size(), courseId);
            return ModulesResponse.builder().courseId(courseId).courseTitle(course.getTitle()).modules(moduleDtos).build();

        } catch (Exception e) {
            logger.error("Failed to retrieve modules for course ID: {}", courseId, e);
            throw new RuntimeException("Failed to retrieve modules. Please try again later.");
        }
    }

    @Transactional
    public String updateModules(UpdateModuleRequest request) {
        try {
            logger.info("Updating modules for course ID: {}", request.getCourseId());

            // Fetch the course
            Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> {
                logger.error("Course not found for ID: {}", request.getCourseId());
                return new RuntimeException("Course not found");
            });

            // Update course title if changed
            if (request.getTitle() != null) {
                logger.info("Updating course title to '{}'", request.getTitle());
                course.setTitle(request.getTitle());
                courseRepository.save(course);
            }

            // Fetch existing modules for the course
            List<Module> existingModules = moduleRepository.findByCourse_CourseId(request.getCourseId());
            Set<String> incomingModuleIds = request.getModules().stream()
                    .map(ModuleDto::getModuleId)
                    .collect(Collectors.toSet());

            // Identify modules to delete (modules in DB but not in incoming list)
            List<Module> modulesToDelete = existingModules.stream()
                    .filter(module -> !incomingModuleIds.contains(module.getModuleId()))
                    .toList();

            // Delete related lessons and modules
            for (Module module : modulesToDelete) {
                logger.debug("Deleting lessons for module ID: {}", module.getModuleId());
                lessonRepository.deleteAllByModule_ModuleId(module.getModuleId());

                logger.debug("Deleting module ID: {}", module.getModuleId());
                moduleRepository.delete(module);
            }

            // Process the remaining modules (Create/Update)
            for (ModuleDto moduleDto : request.getModules()) {
                Module module = existingModules.stream()
                        .filter(m -> m.getModuleId().equals(moduleDto.getModuleId()))
                        .findFirst()
                        .orElse(new Module());

                module.setModuleId(moduleDto.getModuleId());
                module.setModuleTitle(moduleDto.getModuleTitle());
                module.setLevel(moduleDto.getLevel());
                module.setIndex(moduleDto.getIndex());
                module.setParentModuleId(moduleDto.getParentModuleId());
                module.setStatus("updated");
                module.setCourse(course);

                moduleRepository.save(module);
            }

            logger.info("Modules updated successfully for course ID: {}", request.getCourseId());
            return "Update successful";

        } catch (RuntimeException e) {
            logger.error("Error updating modules for course ID: {}", request.getCourseId(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

}

