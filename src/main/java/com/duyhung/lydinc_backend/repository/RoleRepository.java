package com.duyhung.lydinc_backend.repository;

import com.duyhung.lydinc_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleId(Integer id);
}
