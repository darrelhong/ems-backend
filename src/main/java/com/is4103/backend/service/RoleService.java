package com.is4103.backend.service;

import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findByRoleEnum(RoleEnum roleEnum) {
        Role role = roleRepository.findByRoleEnum(roleEnum);
        return role;
    }
}
