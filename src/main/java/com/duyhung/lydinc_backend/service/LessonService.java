package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Lesson;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.LessonDto;
import com.duyhung.lydinc_backend.repository.*;
import com.duyhung.lydinc_backend.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(LessonService.class);

    // Repositories for handling database interactions
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    /**
     * Updates the lessons in a module by:
     * - Deleting lessons that are no longer in the new list.
     * - Updating existing lessons.
     * - Adding new lessons.
     *
     * @param lessons  List of new or updated lessons.
     * @param moduleId The module to which lessons belong.
     * @return Success message.
     */
    @Transactional
    public String updateLessonData(List<LessonDto> lessons, String moduleId) {
        Logger logger = LoggerFactory.getLogger(getClass());

        // Step 1: Fetch existing lesson IDs for the module
        List<String> existingLessonIds = lessonRepository.findLessonIdsByModuleId(moduleId);
        logger.info("Existing lessons for module {}: {}", moduleId, existingLessonIds);

        // Step 2: Collect incoming lesson IDs from the request
        Set<String> incomingLessonIds = lessons.stream()
                .map(LessonDto::getLessonId)
                .collect(Collectors.toSet());
        logger.info("Incoming lesson IDs: {}", incomingLessonIds);

        // Step 3: DELETE lessons that are not present in the incoming list
        existingLessonIds.stream()
                .filter(id -> !incomingLessonIds.contains(id)) // Find lessons to delete
                .forEach(lessonId -> {
                    lessonRepository.deleteLessonByLessonId(lessonId); // Delete from DB
                    logger.info("Deleted lesson with ID: {}", lessonId);
                });

        // Step 4: ADD or UPDATE lessons
        lessons.forEach(lessonDto -> {
            // Check if the lesson already exists in the database
            String findLesson = lessonRepository.findByLessonId(lessonDto.getLessonId());
            if (findLesson != null) {
                // UPDATE existing lesson
                lessonRepository.updateLessonByLessonId(
                        lessonDto.getLessonId(),
                        lessonDto.getIndex(),
                        lessonDto.getText(),
                        lessonDto.getUrl(),
                        lessonDto.getFileName()
                );
                logger.info("Updated lesson with ID: {}", lessonDto.getLessonId());
            } else {
                // ADD new lesson
                lessonRepository.addLesson(
                        lessonDto.getLessonId(),
                        moduleId,
                        lessonDto.getIndex(),
                        lessonDto.getText(),
                        lessonDto.getType(),
                        lessonDto.getUrl(),
                        lessonDto.getFileName()
                );
                logger.info("Added new lesson with ID: {}", lessonDto.getLessonId());
            }
        });

        logger.info("Lesson update process completed for module {}", moduleId);
        return "Update successfully";
    }

    /**
     * Fetches lesson data for a specific module and course, ensuring the user has access.
     *
     * @param moduleId The module ID for which lessons are retrieved.
     * @param courseId The associated course ID.
     * @return List of lessons in the module.
     */
    public List<LessonDto> getLessonData(String moduleId, Integer courseId) {
        // Retrieve the currently logged-in user's ID
        String userId = SecurityUtils.getUserIdFromAuthentication();

        // Fetch user details from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user has permission to access this course
        validateUserAccess(user, courseId);

        logger.info("Fetching lessons for moduleId: {}", moduleId);

        // Retrieve lessons from the database based on the module ID
        List<Lesson> lessons = lessonRepository.findByModule_ModuleId(moduleId);

        // Handle case where no lessons are found
        if (lessons.isEmpty()) {
            logger.warn("No lessons found for moduleId: {}", moduleId);
            return Collections.emptyList();
        }

        logger.info("Found {} lessons for moduleId: {}", lessons.size(), moduleId);

        // Convert entity list to DTO list
        return lessons.stream().map(this::mapToLessonDto).toList();
    }

    /**
     * Validates whether a user has permission to access a course.
     * - If the user has an admin role, they must be enrolled in the course.
     * - If the user is a lecturer, they must be assigned to the course.
     *
     * @param user     The user requesting access.
     * @param courseId The course ID being accessed.
     */
    private void validateUserAccess(User user, Integer courseId) {
        boolean isAllowed;

        if (user.getRoles().stream().anyMatch(role -> role.getRoleId().equals(1))) {
            // Admin access check: Verify enrollment based on university association
            isAllowed = (user.getUniversity() != null)
                    ? enrollmentRepository.checkExistUser(user.getUniversity().getUniversityId(), courseId)
                    : userCourseRepository.checkExistUser(user.getUserId(), courseId);
        } else {
            // Lecturer access check: Ensure user is assigned as a lecturer
            isAllowed = courseRepository.isLecturer(user.getUserId(), courseId);
        }

        // If access is not allowed, throw an exception
        if (!isAllowed) {
            throw new RuntimeException("You're not allowed to access the course");
        }
    }
}
