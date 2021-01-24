package com.is4103.backend.service;

import java.util.ArrayList;

import com.is4103.backend.model.User;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.util.validation.errors.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) {
        try {
            final User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User does not exist");
            }

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                    new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
