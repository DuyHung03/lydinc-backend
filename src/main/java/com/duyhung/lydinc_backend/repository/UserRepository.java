package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = "select u from User u inner join u.roles r where r.roleId = 1 and u.university.universityId is null ")
    List<User> findAllStudent();

    Optional<User> findByUsername(String username);

    @Query(value = "select new User (" +
            "u.userId, " +
            "u.username, " +
            "u.email, " +
            "u.phone, " +
            "u.password, " +
            "u.photoUrl, " +
            "u.name, " +
            "u.isPasswordChanged, " +
            "u.isAccountGranted" +
            ") from User u where u.username = ?1")
    Optional<User> checkUserExist(String username);

    Optional<List<User>> findByUniversityUniversityId(Integer id);

    @Query("select u from User u where u.userId <> :currentId ")
    Page<User> findAllExceptCurrent(@Param("currentId") String currentId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?1, u.isPasswordChanged = ?2 WHERE u.userId = ?3")
    void saveNewPassword(String newPassword, Integer isPasswordChanged, String userId);

}
