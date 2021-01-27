package com.is4103.backend.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.User;
import com.is4103.backend.model.VerificationToken;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.repository.VerificationTokenRepository;
import com.is4103.backend.util.validation.errors.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository vtRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User registerNewUser(SignupRequest signupRequest, String roleStr) throws UserAlreadyExistsException {
        if (emailExists(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
        }

        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());

        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByName(roleStr);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);

        return userRepository.save(newUser);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken vt = new VerificationToken(token, user);
        vtRepository.save(vt);
    }

    public String validateVerificationToken(String token) {
        VerificationToken vt = vtRepository.findByToken(token);

        if (vt == null) {
            return "Invalid Token";
        }

        User user = vt.getUser();
        Calendar cal = Calendar.getInstance();
        if ((vt.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            vtRepository.delete(vt);
            return "Token Expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid Token";
    }
}
