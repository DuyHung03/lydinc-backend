package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Course;
import com.duyhung.lydinc_backend.model.Enrollment;
import com.duyhung.lydinc_backend.model.Module;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.*;
import com.duyhung.lydinc_backend.repository.CourseRepository;
import com.duyhung.lydinc_backend.repository.EnrollmentRepository;
import com.duyhung.lydinc_backend.repository.ModuleRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    public Integer createNewCourse(String title, List<ModuleDto> modules, String lecturerId) {

        try {
            // Save the course
            Course course = courseRepository.save(
                    Course.builder()
                            .title(title)
                            .lecturerId(lecturerId)
                            .build()
            );

            // Save all modules
            modules.forEach(module ->
                    moduleRepository.save(Module.builder()
                            .moduleId(module.getModuleId())
                            .moduleTitle(module.getModuleTitle())
                            .level(module.getLevel())
                            .index(module.getIndex())
                            .parentModuleId(module.getParentModuleId())
                            .course(course)
                            .build())
            );

            // Return the course ID
            return course.getCourseId();
        } catch (Exception e) {
            // Log the error and rethrow or return a meaningful error message
            throw new RuntimeException("Failed to create course: " + e.getMessage(), e);
        }
    }

    public List<CourseDto> getCourseByLecturer(String lecturerId) {
        List<Course> courses = courseRepository.findByLecturerId(lecturerId).orElseThrow(() -> new RuntimeException("Course not found"));

        return courses.stream().map(course ->

                CourseDto.builder().courseId(course.getCourseId()).title(course.getTitle()).enrollmentDate(course.getEnrollmentDate()).status(course.getStatus()).lecturerId(course.getLecturerId()).lecturerName(course.getLecturerName()).lecturerEmail(course.getLecturerEmail()).lecturerPhoto(course.getLecturerPhoto()).enrollments(mapEnrollmentsToDtos(course.getEnrollments())).build()).collect(Collectors.toList());
    }

    public List<CourseDto> getCourseByStudent(String studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserUserId(studentId);

        return enrollments.stream().map(this::mapToCourseDto).collect(Collectors.toList());
    }

    //

    private CourseDto mapToCourseDto(Enrollment enrollment) {
        return CourseDto.builder().courseId(enrollment.getCourse().getCourseId()).title(enrollment.getCourse().getTitle()).status(enrollment.getCourse().getStatus()).enrollmentDate(enrollment.getEnrollmentDate()).lecturerId(enrollment.getCourse().getLecturerId()).lecturerEmail(enrollment.getCourse().getLecturerEmail()).lecturerName(enrollment.getCourse().getLecturerName()).lecturerPhoto(enrollment.getCourse().getLecturerPhoto()).build();
    }


    public List<EnrollmentDto> mapEnrollmentsToDtos(List<Enrollment> enrollments) {
        return enrollments.stream().map(this::mapEnrollmentToDto).collect(Collectors.toList());
    }

    public EnrollmentDto mapEnrollmentToDto(Enrollment enrollment) {
        return EnrollmentDto.builder().enrollmentId(enrollment.getEnrollmentId()).enrollmentDate(enrollment.getEnrollmentDate()).status(enrollment.getStatus()).user(mapUserToDto(enrollment.getUser()))  // Mapping the user inside Enrollment
                .build();
    }

    public UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .photoUrl(user.getPhotoUrl())
                .name(user.getName())
                .isPasswordFirstChanged(user.getIsPasswordFirstChanged())
                .isAccountGranted(user.getIsAccountGranted())
                .university(UniversityDto.builder()
                        .universityId(user.getUniversity().getUniversityId())
                        .shortName(user.getUniversity().getShortName())
                        .fullName(user.getUniversity().getFullName())
                        .build()
                )
                .build();
    }

}


