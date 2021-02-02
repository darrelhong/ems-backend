package com.is4103.backend.repository;

import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleEnum(RoleEnum roleEnum);
}
