package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Enrollment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    @Query(value = "select e.enrollmentId from Enrollment e where e.course.courseId = ?1 and e.university.universityId = ?2")
    Integer findEnrollmentExists(Integer courseId, Integer universityId);

    @Query(value = "select e.enrollmentId from Enrollment e")
    List<Integer> findAllEnrollmentIds();

    @Modifying
    @Transactional
    @Query(value = "delete from Enrollment e where e.enrollmentId = ?1")
    void deleteByEnrollmentId(Integer id);

}
