package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Course;
import com.duyhung.lydinc_backend.model.Enrollment;
import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.repository.EnrollmentRepository;
import com.duyhung.lydinc_backend.repository.UniversityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UniversityRepository universityRepository;

    @Transactional
    public void assignUniversityToCourse(List<Integer> universityIds, Course course) {
        universityIds.forEach(id -> {
            Integer findEnrollmentId = enrollmentRepository.findEnrollmentExists(course.getCourseId(), id);
            if (findEnrollmentId != null) {
                return;
            }
            University university = universityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("University not found with ID: " + id));
            enrollmentRepository.save(Enrollment.builder().university(university).course(course).status(1).build());
        });
    }


    public Enrollment getEnrollmentById(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }
}
