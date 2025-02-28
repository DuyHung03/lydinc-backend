package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Course;
import com.duyhung.lydinc_backend.model.Enrollment;
import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.repository.EnrollmentRepository;
import com.duyhung.lydinc_backend.repository.UniversityRepository;
import com.duyhung.lydinc_backend.repository.UserCourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final UniversityRepository universityRepository;
    private final UserCourseRepository userCourseRepository;
    private final NotificationService notificationService;

    @Transactional
    public void assignUniversityToCourse(
            List<Integer> universityIds,
            List<Integer> deleteUniversityIds,
            Course course,
            List<String> userIds
    ) {
        logger.info("Starting assignment of universities and users to course with ID: {}", course.getCourseId());

        // Deleting enrollments
        deleteUniversityIds.forEach(id -> {
            Integer findEnrollmentId = enrollmentRepository.findEnrollmentExists(course.getCourseId(), id);
            if (findEnrollmentId != null) {
                logger.info("Deleting enrollment with ID: {} for university ID: {}", findEnrollmentId, id);
                enrollmentRepository.deleteByEnrollmentId(findEnrollmentId);
            } else {
                logger.warn("No enrollment found for university ID: {} in course ID: {}", id, course.getCourseId());
            }
        });

        // Adding enrollments
        universityIds.forEach(id -> {
            Integer findEnrollmentId = enrollmentRepository.findEnrollmentExists(course.getCourseId(), id);
            if (findEnrollmentId != null) {
                logger.info("Skipping enrollment as it already exists for university ID: {} in course ID: {}", id, course.getCourseId());
                return;
            }

            University university = universityRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("University not found with ID: {}", id);
                        return new RuntimeException("University not found with ID: " + id);
                    });

            Enrollment newEnrollment = Enrollment.builder().university(university).course(course).build();
            enrollmentRepository.save(newEnrollment);
            logger.info("Successfully enrolled university ID: {} into course ID: {}", id, course.getCourseId());

            notificationService.sendNotificationToUniversityStudents(id, "You have just been added to a new course.");
        });

        // Removing user-course enrollments
        logger.info("Removing all user enrollments for course ID: {}", course.getCourseId());
        userCourseRepository.deleteAllByCourseId(course.getCourseId());

        // Adding new user-course enrollments
        userIds.forEach(id -> {
            logger.info("Enrolling user ID: {} into course ID: {}", id, course.getCourseId());
            userCourseRepository.insertUserEnrollment(course.getCourseId(), id);
            notificationService.sendNotificationToCourseStudents(id, "You have just been added to a new course.");
        });

        logger.info("Completed assignment of universities and users to course ID: {}", course.getCourseId());
    }

    public Enrollment getEnrollmentById(Integer enrollmentId) {
        logger.info("Fetching enrollment by ID: {}", enrollmentId);
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> {
                    logger.error("Enrollment not found with ID: {}", enrollmentId);
                    return new RuntimeException("Enrollment not found");
                });
    }
}
