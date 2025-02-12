package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Module;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, String> {

    List<Module> findByCourse_CourseId(Integer courseId);

    @Query(value = "select m.moduleId from Module m where m.moduleId = ?1")
    Optional<String> findByModuleId(String moduleId);

    @Modifying
    @Transactional
    @Query(value = "delete from Module m where m.moduleId = ?1")
    void deleteByModuleId(String moduleId);

}
