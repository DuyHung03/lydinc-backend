package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.ExcelPracticeLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelPracticeLinkRepository extends JpaRepository<ExcelPracticeLink, Integer> {
    @Query(value = "select e " +
            "from ExcelPracticeLink e " +
            "where e.studentId = ?1 " +
            "and e.courseId = ?2 " +
            "and e.moduleId = ?3 " +
            "and e.lessonId = ?4")
    ExcelPracticeLink findByStudentId(String studentId, Integer courseId, String moduleId, String lessonId);

    @Modifying
    @Query(value = "delete from ExcelPracticeLink e where e.courseId = ?1")
    void deleteExcelPracticeLinkByCourseId(Integer courseId);


}
