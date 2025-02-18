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

    @Transactional
    public void assignUniversityToCourse(
            List<Integer> universityIds,
            List<Integer> deleteUniversityIds,
            Course course,
            List<String> userIds
    ) {
        deleteUniversityIds.forEach(id -> {
            Integer findEnrollmentId = enrollmentRepository.findEnrollmentExists(course.getCourseId(), id);
            if (findEnrollmentId != null) {
                enrollmentRepository.deleteByEnrollmentId(findEnrollmentId);
            }
        });

        universityIds.forEach(id -> {
            Integer findEnrollmentId = enrollmentRepository.findEnrollmentExists(course.getCourseId(), id);
            if (findEnrollmentId != null) {
                return;
            }
            University university = universityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("University not found with ID: " + id));
            enrollmentRepository.save(Enrollment.builder().university(university).course(course).build());
        });

        userCourseRepository.deleteAllByCourseId(course.getCourseId());
        userIds.forEach(id -> {
            userCourseRepository.insertUserEnrollment(course.getCourseId(), id);
        });
    }
    
    public Enrollment getEnrollmentById(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }
}
