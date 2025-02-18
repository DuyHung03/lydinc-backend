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
    @Query(value = "select u from User u inner join u.roles r where r.roleId = 1 and u.university.universityId is null ")
    List<User> findAllStudent();

//    @Query(value = "select u from User u inner join u.roles r where r.roleId = 1 and u.username like ?1")
//    List<User> findStudentByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query(value = "select u.username from User u where u.username = ?1")
    String checkUserExist(String username);

    Optional<List<User>> findByUniversityUniversityId(Integer id);

    @Query("select u from User u where u.userId <> :currentId ")
    Page<User> findAllExceptCurrent(@Param("currentId") String currentId, Pageable pageable);
}
