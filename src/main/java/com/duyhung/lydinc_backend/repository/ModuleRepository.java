package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, String> {

    List<Module> findByCourse_CourseId(Integer courseId);

}
