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
    public String updateModulesTitle(UpdateModuleRequest request) {
        try {
            logger.info("Updating modules for course ID: {}", request.getCourseId());

            Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> {
                logger.error("Course not found for ID: {}", request.getCourseId());
                return new RuntimeException("Course not found");
            });

            if (request.getTitle() != null) {
                logger.info("Updating course title to '{}'", request.getTitle());
                course.setTitle(request.getTitle());
                courseRepository.save(course);
            }

            request.getCreatedModules().forEach(module -> {
                logger.debug("Creating new module with title: {}", module.getModuleTitle());
                moduleRepository.save(Module.builder().moduleId(module.getModuleId()).moduleTitle(module.getModuleTitle()).level(module.getLevel()).index(module.getIndex()).status("created").parentModuleId(module.getParentModuleId()).course(course).build());
            });

            request.getDeletedModuleIds().forEach(moduleId -> {
                String module = moduleRepository.findByModuleId(moduleId)
                        .orElseThrow(() -> new RuntimeException("Module not found for ID: " + moduleId));

                logger.debug("Deleting related lessons for module ID: {}", moduleId);
                lessonRepository.deleteAllByModule_ModuleId(module);

                logger.debug("Deleting module with ID: {}", moduleId);
                moduleRepository.deleteByModuleId(module);
            });


            request.getUpdatedModules().forEach(module -> {
                Module existingModule = moduleRepository.findById(module.getModuleId()).orElseThrow(() -> {
                    logger.error("Module not found for ID: {}", module.getModuleId());
                    return new RuntimeException("Module not found");
                });
                logger.debug("Updating module ID: {} with new title: {}", module.getModuleId(), module.getModuleTitle());
                existingModule.setModuleTitle(module.getModuleTitle());
                moduleRepository.save(existingModule);
            });

            logger.info("Module updates completed successfully for course ID: {}", request.getCourseId());
            return "Update successfully";

        } catch (RuntimeException e) {
            logger.error("Error during module update for course ID: {}", request.getCourseId(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

