package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Course;
import com.duyhung.lydinc_backend.model.Enrollment;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.CourseDto;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.repository.CourseRepository;
import com.duyhung.lydinc_backend.repository.EnrollmentRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public String assignUsersToCourse(List<String> userIds, Integer courseId) {
        Optional<Course> courseOptional = courseRepository.findByCourseId(courseId);

        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }

        Course course = courseOptional.get();

        List<User> users = userRepository.findAllById(userIds);
        if (users.isEmpty() || users.size() != userIds.size()) {
            throw new IllegalArgumentException("Some users were not found");
        }

        List<Enrollment> enrollments = users.stream()
                .map(user -> Enrollment.builder()
                        .user(user)
                        .course(course)
                        .enrollmentDate(LocalDate.now())
                        .status("ACTIVE")
                        .build())
                .collect(Collectors.toList());

        enrollmentRepository.saveAll(enrollments);
        return "Assign new students to " + course.getTitle() + " successfully";
    }
    
    public Enrollment getEnrollmentById(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

    }

}
