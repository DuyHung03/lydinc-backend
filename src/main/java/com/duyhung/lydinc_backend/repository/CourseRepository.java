package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query(value = "select c.courseId as courseId, c.privacy as privacy ,e.university.universityId as universityId from Course c inner join Enrollment e on c.courseId = e.course.courseId and c.courseId = ?1")
    List<Object[]> findCoursePrivacy(Integer courseId);

    Optional<List<Course>> findByLecturerId(String teacherId);
}
