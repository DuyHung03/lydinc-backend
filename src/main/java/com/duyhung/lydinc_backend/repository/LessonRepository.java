package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Lesson;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    Optional<Lesson> findByModule_ModuleId(String moduleId);

    @Modifying
    @Transactional
    @Query(value = "delete from Lesson l where l.module.moduleId = ?1")
    void deleteAllByModule_ModuleId(String moduleId);
}
