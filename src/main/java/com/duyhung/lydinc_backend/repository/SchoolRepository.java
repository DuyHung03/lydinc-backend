package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
}

