package com.is4103.backend.util.validation;

import java.util.Set;

import javax.transaction.Transactional;

import com.is4103.backend.model.Role;
import com.is4103.backend.model.User;
import com.is4103.backend.repository.RoleRepository;
import com.is4103.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitRunner implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // init roles
        if (roleRepository.findRoleByName("ADMIN") == null) {
            roleRepository.save(new Role("ADMIN", "System Admin"));
        }
        if (roleRepository.findRoleByName("EVNTORG") == null) {
            roleRepository.save(new Role("EVNTORG", "Event Organiser"));
        }
        if (roleRepository.findRoleByName("BIZPTNR") == null) {
            roleRepository.save(new Role("BIZPTNR", "Business Partnr"));
        }
        if (roleRepository.findRoleByName("ATND") == null) {
            roleRepository.save(new Role("ATND", "Attendee"));
        }

        if (userRepository.findByEmail("admin@abc.com") == null) {
            createAdmin();
        }
    }

    @Transactional
    private void createAdmin() {
        User admin = new User();
        admin.setEmail("admin@abc.com");
        admin.setName("Default Admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setEnabled(true);
        admin.setRoles(Set.of(roleRepository.findRoleByName("ADMIN")));
        userRepository.save(admin);
    }
}
