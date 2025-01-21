package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.ExcelPracticeLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelPracticeLinkRepository extends JpaRepository<ExcelPracticeLink, Integer> {
    ExcelPracticeLink findByStudentId(String studentId);
}
