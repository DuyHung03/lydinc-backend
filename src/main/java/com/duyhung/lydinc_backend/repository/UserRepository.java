package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.User;
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
//    @Query(value = "select u " +
//            "from User u left join UserRoles ur on ur.userId = u.userId " +
//            "where ur.roleId = ?1")
//    List<User> findAllByRole(Integer roleId);

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

    @Query("SELECT u FROM User u " +
            "LEFT JOIN UserRoles ur ON u.userId = ur.userId " +
            "LEFT JOIN University uni ON uni.universityId = u.university.universityId " +
            "WHERE ur.roleId = 1 " +
            "AND (:universityId IS NULL OR uni.universityId = :universityId)")
    Page<User> findAllExceptCurrent(@Param("universityId") Integer universityId, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.password = ?1, u.isPasswordChanged = ?2 WHERE u.userId = ?3")
    void saveNewPassword(String newPassword, Integer isPasswordChanged, String userId);

}
