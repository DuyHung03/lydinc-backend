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

    // Repository dependencies for database interactions
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    /**
     * Retrieves all modules associated with a given course.
     *
     * @param courseId The ID of the course.
     * @return ModulesResponse containing course details and its modules.
     */
    public ModulesResponse getModulesByCourse(Integer courseId) {
        try {
            logger.info("Fetching modules for course with ID: {}", courseId);

            // Retrieve all modules associated with the course
            List<Module> modules = moduleRepository.findByCourse_CourseId(courseId);

            // Fetch course details
            Course course = courseRepository.findById(courseId).orElseThrow(() -> {
                logger.error("Course not found for ID: {}", courseId);
                return new RuntimeException("Course not found");
            });

            // Convert Module entities to DTOs for response
            List<ModuleDto> moduleDtos = modules.stream()
                    .map(module -> ModuleDto.builder()
                            .moduleId(module.getModuleId())
                            .moduleTitle(module.getModuleTitle())
                            .level(module.getLevel())
                            .index(module.getIndex())
                            .parentModuleId(module.getParentModuleId())
                            .status(module.getStatus())
                            .courseId(courseId)
                            .build())
                    .toList();

            logger.info("Successfully fetched {} modules for course ID: {}", moduleDtos.size(), courseId);

            // Construct and return the response
            return ModulesResponse.builder()
                    .courseId(courseId)
                    .courseTitle(course.getTitle())
                    .description(course.getDescription())
                    .thumbnail(course.getThumbnail())
                    .modules(moduleDtos)
                    .build();

        } catch (Exception e) {
            logger.error("Failed to retrieve modules for course ID: {}", courseId, e);
            throw new RuntimeException("Failed to retrieve modules. Please try again later.");
        }
    }

    /**
     * Updates modules within a course.
     * - Updates course information (title, description, thumbnail).
     * - Deletes modules that are removed in the request.
     * - Updates existing modules or adds new ones.
     *
     * @param request UpdateModuleRequest containing course and module updates.
     * @return A success message upon completion.
     */
    @Transactional
    public String updateModules(UpdateModuleRequest request) {
        try {
            logger.info("Updating modules for course ID: {}", request.getCourseId());

            // Step 1: Fetch the course from the database
            Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> {
                logger.error("Course not found for ID: {}", request.getCourseId());
                return new RuntimeException("Course not found");
            });

            // Step 2: Update course details if modified
            if (request.getTitle() != null) {
                logger.info("Updating course title to '{}'", request.getTitle());
                course.setTitle(request.getTitle());
                course.setDescription(request.getDescription());
                course.setThumbnail(request.getThumbnail());
                courseRepository.save(course);
            }

            // Step 3: Fetch existing modules in the course
            List<Module> existingModules = moduleRepository.findByCourse_CourseId(request.getCourseId());

            // Step 4: Identify which modules exist in the request
            Set<String> incomingModuleIds = request.getModules().stream()
                    .map(ModuleDto::getModuleId)
                    .collect(Collectors.toSet());

            // Step 5: Find modules that should be deleted (not present in the request)
            List<Module> modulesToDelete = existingModules.stream()
                    .filter(module -> !incomingModuleIds.contains(module.getModuleId()))
                    .toList();

            // Step 6: Delete related lessons and modules
            for (Module module : modulesToDelete) {
                logger.debug("Deleting lessons for module ID: {}", module.getModuleId());
                lessonRepository.deleteAllByModule_ModuleId(module.getModuleId());

                logger.debug("Deleting module ID: {}", module.getModuleId());
                moduleRepository.delete(module);
            }

            // Step 7: Process the remaining modules (Create or Update)
            for (ModuleDto moduleDto : request.getModules()) {
                // Check if the module already exists
                Module module = existingModules.stream()
                        .filter(m -> m.getModuleId().equals(moduleDto.getModuleId()))
                        .findFirst()
                        .orElse(new Module());

                // Update module fields
                module.setModuleId(moduleDto.getModuleId());
                module.setModuleTitle(moduleDto.getModuleTitle());
                module.setLevel(moduleDto.getLevel());
                module.setIndex(moduleDto.getIndex());
                module.setParentModuleId(moduleDto.getParentModuleId());
                module.setStatus("updated");
                module.setCourse(course);

                // Save the module to the database
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
