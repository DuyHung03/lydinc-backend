package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.UserCourse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Integer> {
    @Modifying
    @Transactional
    @Query(value = "delete from UserCourse uc where uc.course.courseId = ?1")
    void deleteAllByCourseId(Integer courseId);

    @Modifying
    @Transactional
    @Query(value = "insert into user_course (course_id, user_id) values (?1, ?2)", nativeQuery = true)
    void insertUserEnrollment(Integer courseId, String userId);

    @Query(value = "select " +
            "case " +
            "when count(uc) > 0 " +
            "then true " +
            "else false " +
            "end " +
            "from UserCourse uc where uc.user.userId = ?1 and uc.course.courseId=?2")
    boolean checkExistUser(String userId, Integer courseId);

}
