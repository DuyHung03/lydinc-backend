package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    @Query(value = "select l from Lesson l where l.module.moduleId = ?1")
    List<Lesson> findByModule_ModuleId(String moduleId);

    @Query(value = "select l.lessonId from Lesson l where l.module.moduleId = ?1")
    List<String> findLessonIdsByModuleId(String moduleId);

    @Modifying
    @Query(value = "insert into lesson (lesson_id, module_id, index, text, type, url, file_name) values (?1,?2,?3,?4,?5,?6,?7)", nativeQuery = true)
    void addLesson(
            String lessonId,
            String moduleId,
            Integer index,
            String text,
            Integer type,
            String url,
            String fileName
    );

    @Query(value = "select l.lessonId from Lesson l where l.lessonId = ?1")
    String findByLessonId(String lessonId);

    @Modifying
    @Query(value = "update Lesson l set l.index = ?2, l.text = ?3, l.url = ?4, l.fileName = ?5 where l.lessonId = ?1")
    void updateLessonByLessonId(
            String lessonId,
            Integer index,
            String text,
            String url,
            String fileName
    );

    @Modifying
    @Query(value = "delete from Lesson l where l.module.moduleId = ?1")
    void deleteAllByModule_ModuleId(String moduleId);

    @Modifying
    @Query(value = "delete from Lesson l where l.lessonId = ?1")
    void deleteLessonByLessonId(String lessonId);

    @Modifying
    @Query(value = "delete from Lesson l where l.module.course.courseId = ?1")
    void deleteLessonByCourseId(Integer courseId);

}
