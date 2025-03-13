package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
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

    @Query(value = "select users.* from users " +
            "left join user_roles on users.user_id = user_roles.user_id " +
            "where user_roles.role_id = 1", nativeQuery = true)
    Page<User> findAllStudents(Pageable pageable);

    @Query(value = "select users.* from users " +
            "left join user_roles on users.user_id = user_roles.user_id " +
            "left join university on users.university_id = university.university_id " +
            "where user_roles.role_id = 1 " +
            "and university.university_id = ?1", nativeQuery = true)
    Page<User> findByUniversityId(Integer universityId, Pageable pageable);

    @Query(value = "SELECT users.* FROM users " +
            "left join user_roles on users.user_id = user_roles.user_id " +
            "WHERE users.username LIKE CONCAT('%', ?1, '%')" +
            "and user_roles.role_id = 1", nativeQuery = true)
    Page<User> searchByUsername(String username, Pageable pageable);

    @Query(value = "select users.* from users " +
            "left join user_roles on users.user_id = user_roles.user_id " +
            "where user_roles.role_id = 1 " +
            "and users.university_id is null ", nativeQuery = true)
    List<User> findStudentsWithoutUniversity();

    @Query(value = "select users.* from users " +
            "left join user_roles on users.user_id = user_roles.user_id " +
            "where user_roles.role_id = 2 ", nativeQuery = true)
    List<User> findAllLecturers();


    @Modifying
    @Query("UPDATE User u SET u.password = ?1, u.isPasswordChanged = ?2 WHERE u.userId = ?3")
    void saveNewPassword(String newPassword, Integer isPasswordChanged, String userId);

}
