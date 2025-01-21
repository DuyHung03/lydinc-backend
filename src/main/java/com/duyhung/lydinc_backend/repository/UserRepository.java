package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<List<User>> findByUniversityUniversityId(Integer id);

    @Query("select u from User u where u.userId <> :currentId ")
    Page<User> findAllExceptCurrent(@Param("currentId") String currentId, Pageable pageable);
}
