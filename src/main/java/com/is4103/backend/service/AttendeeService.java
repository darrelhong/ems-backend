package com.is4103.backend.service;

import com.is4103.backend.controller.BusinessPartnerController;
import com.is4103.backend.controller.EventOrganiserController;
import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdateAttendeeRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.repository.AttendeeRepository;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

@Service
public class AttendeeService {

    @Autowired
    private AttendeeRepository atnRepository;

    @Autowired
    private EventOrganiserRepository eoRepository;

    @Autowired
    private EventOrganiserController eoController;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BusinessPartnerController bpController;

    @Autowired
    private BusinessPartnerRepository bpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<Attendee> getAllAttendees() {
        return atnRepository.findAll();
    }

    public Attendee getAttendeeById(Long id) {
        return atnRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Attendee not found"));
    }

    public Attendee getAttendeeByEmail(String email) throws UserNotFoundException {
        Attendee attendee = atnRepository.findByEmail(email);
        if (attendee == null) {
            throw new UserNotFoundException("Attendee not found");
        }
        return attendee;
    }

    public List<BusinessPartner> getFollowingBp(Long id) {
        Attendee attendee = getAttendeeById(id);
        List<BusinessPartner> following = new ArrayList<>();
        following = attendee.getFollowedBusinessPartners();
        return following;
    }

    public List<EventOrganiser> getFollowingEo(Long id) {
        Attendee attendee = getAttendeeById(id);
        List<EventOrganiser> following = new ArrayList<>();
        following = attendee.getFollowedEventOrgs();
        return following;
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

    @Transactional
    public Attendee followBusinessPartner(Attendee user, FollowRequest followRequest) {

        BusinessPartner bp = bpController.getBusinessPartnerById(followRequest.getId());
        List<BusinessPartner> follow = user.getFollowedBusinessPartners();
        follow.add(bp);
        user.setFollowedBusinessPartners(follow);
        List<Attendee> followers = bp.getAttendeeFollowers();
        followers.add(user);
        bpRepository.save(bp);
        return atnRepository.save(user);
    }

    @Transactional
    public Attendee unfollowBusinessPartner(Attendee user, FollowRequest followRequest) {

        BusinessPartner bp = bpController.getBusinessPartnerById(followRequest.getId());
        List<BusinessPartner> follow = user.getFollowedBusinessPartners();
        follow.remove(bp);
        user.setFollowedBusinessPartners(follow);
        List<Attendee> followers = bp.getAttendeeFollowers();
        followers.remove(user);
        bpRepository.save(bp);
        return atnRepository.save(user);
    }

    @Transactional
    public Attendee followEventOrganiser(Attendee user, FollowRequest followRequest) {

        EventOrganiser eo = eoController.getEventOrganiserById(followRequest.getId());
        List<EventOrganiser> follow = user.getFollowedEventOrgs();
        follow.add(eo);
        user.setFollowedEventOrgs(follow);
        List<Attendee> followers = eo.getAttendeeFollowers();
        followers.add(user);
        eoRepository.save(eo);
        return atnRepository.save(user);
    }

    @Transactional
    public Attendee unfollowEventOrganiser(Attendee user, FollowRequest followRequest) {

        EventOrganiser eo = eoController.getEventOrganiserById(followRequest.getId());
        List<EventOrganiser> follow = user.getFollowedEventOrgs();
        follow.remove(eo);
        user.setFollowedEventOrgs(follow);
        List<Attendee> followers = eo.getAttendeeFollowers();
        followers.remove(user);
        eoRepository.save(eo);
        return atnRepository.save(user);
    }

}
