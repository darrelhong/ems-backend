package com.is4103.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BusinessPartnerService {

    @Autowired
    private BusinessPartnerRepository bpRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<BusinessPartner> getAllBusinessPartners() {
        return bpRepository.findAll();
    }

    public Page<BusinessPartner> getBusinessPartnersPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return bpRepository.findAll(pageRequest);
    }

    public BusinessPartner getBusinessPartnerById(Long id) {
        return bpRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    @Transactional
    public BusinessPartner registerNewBusinessPartner(SignupRequest signupRequest, boolean enabled)
            throws UserAlreadyExistsException {
        if (userService.emailExists(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
        }

        BusinessPartner newBp = new BusinessPartner();
        newBp.setName(signupRequest.getName());
        newBp.setEmail(signupRequest.getEmail());

        newBp.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByRoleEnum(RoleEnum.BIZPTNR);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newBp.setRoles(roles);

        if (enabled) {
            newBp.setEnabled(true);
        }

        if (enabled) {
            newBp.setEnabled(true);
        }

        newBp = bpRepository.save(newBp);

        if (!newBp.isEnabled()) {
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(newBp));
        }

        return newBp;
    }
}
