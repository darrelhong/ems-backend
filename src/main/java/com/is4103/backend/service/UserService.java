package com.is4103.backend.service;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.model.User;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.util.validation.errors.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User registerNewUser(SignupRequest signupRequest) throws UserAlreadyExistsException {
        if (emailExists(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
        }

        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());

        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setRoles(Set.of("ROLE_ADMIN"));
        return userRepository.save(newUser);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
