package com.is4103.backend.service;

import com.is4103.backend.model.Role;
import com.is4103.backend.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findByName(String name) {
        Role role = roleRepository.findRoleByName(name);
        return role;
    }
}
