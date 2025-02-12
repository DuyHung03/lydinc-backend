package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Course;
import com.duyhung.lydinc_backend.model.Module;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.CourseDto;
import com.duyhung.lydinc_backend.model.dto.CoursePrivacy;
import com.duyhung.lydinc_backend.model.dto.ModuleDto;
import com.duyhung.lydinc_backend.repository.CourseRepository;
import com.duyhung.lydinc_backend.repository.EnrollmentRepository;
import com.duyhung.lydinc_backend.repository.ModuleRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModuleRepository moduleRepository;
    private final EnrollmentService enrollmentService;

    public List<CourseDto> getCourseByLecturer(String lecturerId) {
        logger.info("Fetching courses for lecturer with ID '{}'", lecturerId);
        List<Course> courses = courseRepository.findByLecturerId(lecturerId).orElseThrow(() -> {
            logger.warn("No courses found for lecturer '{}'", lecturerId);
            return new RuntimeException("Course not found");
        });

        User lecturer = userRepository.findById(lecturerId).orElseThrow(() -> {
            logger.warn("Lecturer '{}' not found", lecturerId);
            return new RuntimeException("Lecturer not found");
        });

        logger.info("Found {} courses for lecturer '{}'", courses.size(), lecturerId);

        return courses.stream().map(course -> CourseDto.builder().courseId(course.getCourseId()).title(course.getTitle()).enrollmentDate(course.getEnrollmentDate()).status(course.getStatus()).lecturerId(lecturerId).lecturerName(lecturer.getUsername()).lecturerEmail(lecturer.getEmail()).lecturerPhoto(lecturer.getPhotoUrl()).privacy(course.getPrivacy()).build()).collect(Collectors.toList());
    }

    public String getCourseByStudent(String studentId) {
//        logger.info("Fetching courses for student with ID '{}'", studentId);
//
//        List<Enrollment> enrollments = enrollmentRepository.findByUserUserId(studentId);
//
//        if (enrollments.isEmpty()) {
//            logger.warn("No enrollments found for student '{}'", studentId);
//        } else {
//            logger.info("Found {} enrollments for student '{}'", enrollments.size(), studentId);
//        }
//
//        return enrollments.stream().map(this::mapToCourseDto).collect(Collectors.toList());
        return ";";
    }

    @Transactional
    public String createNewCourse(
            String title,
            List<ModuleDto> modules,
            String lecturerId
    ) {
        logger.info("Creating a new course with title '{}' for lecturer '{}'", title, lecturerId);

        // Save course
        Course course = courseRepository.save(
                Course.builder().title(title).lecturerId(lecturerId).privacy("public")
                        .build()
        );

        logger.info("Course '{}' saved successfully.", course.getCourseId());

        // Save all modules
        modules.forEach(module -> {
            moduleRepository.save(
                    Module.builder().moduleId(module.getModuleId())
                            .moduleTitle(module.getModuleTitle()).level(module.getLevel())
                            .index(module.getIndex()).status("created")
                            .parentModuleId(module.getParentModuleId())
                            .course(course)
                            .build()
            );
            logger.info("Module '{}' for course '{}' saved successfully.",
                    module.getModuleId(),
                    course.getCourseId());
        });

        return "Course and modules created successfully.";
    }

    @Transactional
    public void editCoursePrivacy(
            String privacy,
            Integer courseId,
            List<Integer> universityIds,
            List<Integer> deleteUniversityIds
    ) {
        // Use the provided course if available; otherwise, fetch from DB
        Course targetCourse = courseRepository.findById(courseId).orElseThrow(() -> {
            logger.warn("No course found with ID {}", courseId);
            return new RuntimeException("Course not found");
        });

        targetCourse.setPrivacy(privacy);
        courseRepository.save(targetCourse);

        if (!"public".equals(privacy)) {
            enrollmentService.assignUniversityToCourse(universityIds, deleteUniversityIds, targetCourse);
        }

        logger.info("Privacy settings updated for course '{}'", targetCourse.getCourseId());
    }


    public CoursePrivacy getCoursePrivacy(Integer courseId) {
        logger.info("Fetching courseId, privacy, and universityIds for courseId: {}", courseId);

        List<Object[]> result = courseRepository.findCoursePrivacy(courseId);

        if (result.isEmpty()) {
            logger.error("No data found for courseId: {}", courseId);
            return CoursePrivacy.builder()
                    .courseId(courseId)
                    .privacy("public")
                    .universityIds(Collections.emptyList())
                    .build();
        }

        Integer fetchedCourseId = (Integer) result.get(0)[0];
        String privacy = (String) result.get(0)[1];
        List<Integer> universityIds = result.stream()
                .map(row -> (Integer) row[2])
                .filter(Objects::nonNull)
                .toList();

        logger.info("Fetched courseId: {}, privacy: {}, universityIds: {}",
                fetchedCourseId, privacy, universityIds);

        return new CoursePrivacy(fetchedCourseId, privacy, universityIds);
    }


}
