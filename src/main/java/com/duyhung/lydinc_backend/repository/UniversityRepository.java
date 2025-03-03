package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversityRepository extends JpaRepository<University, Integer> {
    @Query(value = "select new University(un.universityId, un.shortName, un.fullName, un.logo, un.location, count(us.userId)) " +
            "from University un " +
            "left join User us on un.universityId = us.university.universityId " +
            "group by un.universityId")
    List<University> findAllUniversities();
}

