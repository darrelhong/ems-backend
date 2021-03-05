package com.is4103.backend.service;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdateAttendeeRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.repository.AttendeeRepository;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

@Service
public class AttendeeService {

    @Autowired
    private AttendeeRepository atnRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<Attendee> getAllAttendees() {
        return atnRepository.findAll();
    }

    public Attendee getAttendeeById(Long id) {
        return atnRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    @Transactional
    public Attendee registerNewAttendee(SignupRequest signupRequest, boolean enabled)
            throws UserAlreadyExistsException {
        if (userService.emailExists(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
        }

        Attendee atn = new Attendee();
        atn.setName(signupRequest.getName());
        atn.setEmail(signupRequest.getEmail());

        atn.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByRoleEnum(RoleEnum.ATND);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        atn.setRoles(roles);

        if (enabled) {
            atn.setEnabled(true);
        }

        if (enabled) {
            atn.setEnabled(true);
        }

        atn = atnRepository.save(atn);

        if (!atn.isEnabled()) {
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(atn));
        }

        return atn;
    }

    @Transactional
    public Attendee updateAttendee(Attendee user, UpdateAttendeeRequest updateAttendeeRequest) {

        user.setName(updateAttendeeRequest.getName());
        user.setDescription(updateAttendeeRequest.getDescription());
        user.setAddress(updateAttendeeRequest.getAddress());
        user.setPhonenumber(updateAttendeeRequest.getPhonenumber());
        user.setCategoryPreferences(updateAttendeeRequest.getCategoryPreferences());

        return atnRepository.save(user);
    }

}
