package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query(value = "select c.courseId as courseId, c.privacy as privacy ,e.university.universityId as universityId from Course c inner join Enrollment e on c.courseId = e.course.courseId and c.courseId = ?1")
    List<Object[]> findCoursePrivacyUniversity(Integer courseId);

    @Query(value = "select c.courseId as courseId, c.privacy as privacy ,uc.user.userId as userId from Course c inner join UserCourse uc on c.courseId = uc.course.courseId and c.courseId = ?1")
    List<Object[]> findCoursePrivacyUser(Integer courseId);

    Optional<Page<Course>> findByLecturerId(String teacherId, Pageable pageable);

    @Query(value = "select new Course(c.courseId, c.title) from Course c join Enrollment e on c.courseId = e.course.courseId where e.university.universityId = ?1")
    List<Course> findByUniversityId(Integer universityId);

    @Query(value = "select new Course(c.courseId, c.title) from Course c join UserCourse uc on c.courseId = uc.course.courseId where uc.user.userId = ?1")
    List<Course> findByUserId(String userId);

    @Query(value = "select case when count (c) > 0 then true else false end from Course c where c.lecturerId = ?1 and c.courseId = ?2")
    boolean isLecturer(String userId, Integer courseId);
}
