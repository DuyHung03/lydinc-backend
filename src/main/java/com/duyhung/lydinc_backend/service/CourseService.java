package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Course;
import com.duyhung.lydinc_backend.model.Module;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.CourseDto;
import com.duyhung.lydinc_backend.model.dto.CoursePrivacy;
import com.duyhung.lydinc_backend.model.dto.ModuleDto;
import com.duyhung.lydinc_backend.model.dto.PaginationResponse;
import com.duyhung.lydinc_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CourseService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final UserCourseRepository userCourseRepository;
    private final LessonRepository lessonRepository;
    private final ExcelPracticeLinkRepository excelPracticeLinkRepository;

    /**
     * Fetches paginated courses for a lecturer.
     */
    public PaginationResponse<CourseDto> getCourseByLecturer(String lecturerId, int pageNo, int pageSize) {
        logger.info("Fetching courses for lecturer with ID '{}'", lecturerId);

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Course> coursesPage = courseRepository.findByLecturerId(lecturerId, pageable).orElseThrow(() -> {
            logger.warn("No courses found for lecturer '{}'", lecturerId);
            return new RuntimeException("Course not found");
        });

        List<Course> courses = coursesPage.getContent();
        User lecturer = userRepository.findById(lecturerId).orElseThrow(() -> {
            logger.warn("Lecturer '{}' not found", lecturerId);
            return new RuntimeException("Lecturer not found");
        });

        logger.info("Found {} courses for lecturer '{}'", courses.size(), lecturerId);

        // Convert Course entities to DTOs
        List<CourseDto> courseDtos = courses.stream().map(course -> CourseDto.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnail(course.getThumbnail())
                .enrollmentDate(course.getEnrollmentDate())
                .status(course.getStatus())
                .lecturerId(lecturerId)
                .lecturerName(lecturer.getUsername())
                .lecturerEmail(lecturer.getEmail())
                .lecturerPhoto(lecturer.getPhotoUrl())
                .privacy(course.getPrivacy())
                .build()
        ).toList();

        return new PaginationResponse<>(courseDtos, coursesPage.getTotalPages(), pageNo + 1, pageSize);
    }

    /**
     * Fetches courses for a student based on student ID and optionally university ID.
     */
    public List<Course> getCourseByStudent(String studentId, Integer universityId) {
        logger.info("Fetching courses for student with ID '{}'", studentId);

        return (universityId != null)
                ? courseRepository.findByUniversityId(universityId)  // Fetch by university ID
                : courseRepository.findByUserId(studentId);          // Fetch by student ID
    }

    /**
     * Creates a new course along with its modules.
     */
    @Transactional
    public String createNewCourse(String title, List<ModuleDto> modules, String description, String thumbnail, String lecturerId) {
        logger.info("Creating a new course with title '{}' for lecturer '{}'", title, lecturerId);

        Course course = courseRepository.save(
                Course.builder().title(title).lecturerId(lecturerId).privacy("public")
                        .description(description).thumbnail(thumbnail).build()
        );

        logger.info("Course '{}' saved successfully.", course.getCourseId());

        // Save modules related to the course
        modules.forEach(module -> {
            moduleRepository.save(Module.builder()
                    .moduleId(module.getModuleId()).moduleTitle(module.getModuleTitle())
                    .level(module.getLevel()).index(module.getIndex()).status("created")
                    .parentModuleId(module.getParentModuleId()).course(course).build()
            );
            logger.info("Module '{}' for course '{}' saved successfully.", module.getModuleId(), course.getCourseId());
        });

        return "Course and modules created successfully.";
    }

    /**
     * Updates the privacy settings of a course.
     */
    @Transactional
    public void editCoursePrivacy(String privacy, Integer courseId, List<Integer> universityIds,
                                  List<Integer> deleteUniversityIds, List<String> userIds) {
        logger.info("Updating privacy for course '{}'", courseId);

        Course targetCourse = courseRepository.findById(courseId).orElseThrow(() -> {
            logger.warn("No course found with ID {}", courseId);
            return new RuntimeException("Course not found");
        });

        targetCourse.setPrivacy(privacy);
        courseRepository.save(targetCourse);
        logger.info("Updated privacy to '{}' for course '{}'", privacy, courseId);

        if (!"public".equals(privacy)) {
            enrollmentService.assignUniversityToCourse(universityIds, deleteUniversityIds, targetCourse, userIds);
        }
    }

    /**
     * Retrieves privacy details for a given course.
     */
    public CoursePrivacy getCoursePrivacy(Integer courseId) {
        logger.info("Fetching privacy settings for course '{}'", courseId);

        List<Object[]> universityRecords = courseRepository.findCoursePrivacyUniversity(courseId);
        List<Object[]> userRecords = courseRepository.findCoursePrivacyUser(courseId);

        if (universityRecords.isEmpty() && userRecords.isEmpty()) {
            logger.error("No privacy data found for course '{}'", courseId);
            return CoursePrivacy.builder()
                    .courseId(courseId).privacy("public")
                    .universityIds(Collections.emptyList()).userIds(Collections.emptyList()).build();
        }

        // Determine privacy from records
        String privacy = !universityRecords.isEmpty() ? (String) universityRecords.get(0)[1] : (String) userRecords.get(0)[1];

        List<Integer> universityIds = universityRecords.stream().map(row -> (Integer) row[2]).filter(Objects::nonNull).toList();
        List<String> userIds = userRecords.stream().map(row -> (String) row[2]).filter(Objects::nonNull).toList();

        logger.info("Privacy for course '{}': '{}', universityIds: {}, userIds: {}", courseId, privacy, universityIds, userIds);

        return CoursePrivacy.builder()
                .courseId(courseId).privacy(privacy)
                .universityIds(universityIds).userIds(userIds).build();
    }

    /**
     * Deletes a course and its related data.
     */
    @Transactional
    public void deleteCourse(Integer courseId) {
        logger.info("Deleting course '{}'", courseId);

        enrollmentRepository.deleteByCourseId(courseId); // Remove enrollments
        userCourseRepository.deleteAllByCourseId(courseId); // Remove user-course relations
        lessonRepository.deleteLessonByCourseId(courseId); // Remove associated lessons
        moduleRepository.deleteByCourseId(courseId); // Remove modules
        excelPracticeLinkRepository.deleteExcelPracticeLinkByCourseId(courseId); // Remove practice links
        courseRepository.deleteById(courseId); // Finally, delete the course

        logger.info("Successfully deleted course '{}'", courseId);
    }
}
