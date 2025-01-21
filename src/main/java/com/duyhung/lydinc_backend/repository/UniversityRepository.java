package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University, Integer> {
}

