package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

    Optional<Lesson> findByModule_ModuleId(String moduleId);

}
