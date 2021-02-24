package com.is4103.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdatePartnerRequest;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventBoothTransaction;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventRepository;
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
    private EventRepository eventRepository;
   

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<BusinessPartner> getAllBusinessPartners() {
        return bpRepository.findAll();
    }




    public List<Event> getAllEvents(Long id) {
        // BusinessPartner partner = new BusinessPartner();
        // partner = getBusinessPartnerById(id);
        // System.out.println("partner " + partner );
        // List<EventBoothTransaction> transactions = new ArrayList<>();
        // System.out.println("transactions " + partner.getEventBoothTransactions());
        // transactions = partner.getEventBoothTransactions();
        // List<Event>  events = new ArrayList<>();
        // if(!transactions.isEmpty()){
        //     for(int i=0; i<transactions.size(); i++){
        //     events.add(transactions.get(i).getEvent());
        //     }
        // }
        
        // return events;
        
        List<Event> events =eventRepository.findAll();
        // System.out.println("events" + events);


        return events;
       
    }

    public Page<BusinessPartner> getBusinessPartnersPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return bpRepository.findAll(pageRequest);
    }

    public BusinessPartner getBusinessPartnerById(Long id) {
        return bpRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    public BusinessPartner getPartnerByEmail(String email) {
        return bpRepository.findByEmail(email);
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

    @Transactional
    public BusinessPartner updatePartner(BusinessPartner user, UpdatePartnerRequest updatePartnerRequest) {
       
        user.setName(updatePartnerRequest.getName());
        user.setDescription(updatePartnerRequest.getDescription());
        user.setAddress(updatePartnerRequest.getAddress());
        user.setPhonenumber(updatePartnerRequest.getPhonenumber());
        user.setBusinessCategory(updatePartnerRequest.getBusinessCategory());

        return bpRepository.save(user);
    }


}
