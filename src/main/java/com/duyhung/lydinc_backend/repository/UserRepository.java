package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<List<User>> findBySchoolSchoolId(Integer id);
}
