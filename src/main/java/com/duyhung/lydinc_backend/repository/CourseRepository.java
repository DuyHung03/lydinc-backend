package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByCourseId(Integer courseId);

    Optional<List<Course>> findByLecturerId(String teacherId);


}
