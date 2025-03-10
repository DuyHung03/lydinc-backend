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
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public String updateLessonData(List<LessonDto> lessons, String moduleId) {
        Logger logger = LoggerFactory.getLogger(getClass());

        // Get existing lessons from DB by moduleId
        List<String> existingLessonIds = lessonRepository.findLessonIdsByModuleId(moduleId);
        logger.info("Existing lessons for module {}: {}", moduleId, existingLessonIds);

        // Collect incoming lesson IDs
        Set<String> incomingLessonIds = lessons.stream()
                .map(LessonDto::getLessonId)
                .collect(Collectors.toSet());
        logger.info("Incoming lesson IDs: {}", incomingLessonIds);

        // DELETE lessons not in the new list
        existingLessonIds.stream()
                .filter(id -> !incomingLessonIds.contains(id))
                .forEach(lessonId -> {
                    lessonRepository.deleteLessonByLessonId(lessonId);
                    logger.info("Deleted lesson with ID: {}", lessonId);
                });

        // ADD or UPDATE lessons
        lessons.forEach(lessonDto -> {
            String findLesson = lessonRepository.findByLessonId(lessonDto.getLessonId());
            if (findLesson != null) {
                // UPDATE
                lessonRepository.updateLessonByLessonId(
                        lessonDto.getLessonId(),
                        lessonDto.getIndex(),
                        lessonDto.getText(),
                        lessonDto.getUrl(),
                        lessonDto.getFileName()
                );
                logger.info("Updated lesson with ID: {}", lessonDto.getLessonId());
            } else {
                // ADD
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


    public List<LessonDto> getLessonData(String moduleId, Integer courseId) {
        String userId = SecurityUtils.getUserIdFromAuthentication();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateUserAccess(user, courseId);

        logger.info("Fetching lessons for moduleId: {}", moduleId);

        List<Lesson> lessons = lessonRepository.findByModule_ModuleId(moduleId);

        if (lessons.isEmpty()) {
            logger.warn("No lessons found for moduleId: {}", moduleId);
            return Collections.emptyList();
        }

        logger.info("Found {} lessons for moduleId: {}", lessons.size(), moduleId);

        return lessons.stream().map(this::mapToLessonDto).toList();
    }

    private void validateUserAccess(User user, Integer courseId) {
        boolean isAllowed;

        if (user.getRoles().stream().anyMatch(role -> role.getRoleId().equals(1))) {
            isAllowed = (user.getUniversity() != null)
                    ? enrollmentRepository.checkExistUser(user.getUniversity().getUniversityId(), courseId)
                    : userCourseRepository.checkExistUser(user.getUserId(), courseId);
        } else {
            isAllowed = courseRepository.isLecturer(user.getUserId(), courseId);
        }

        if (!isAllowed) {
            throw new RuntimeException("You're not allowed to access the course");
        }
    }

}
