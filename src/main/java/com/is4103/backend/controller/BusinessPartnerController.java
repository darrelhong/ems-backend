package com.is4103.backend.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.is4103.backend.dto.DisabledAccountRequest;
import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.SignupResponse;
import com.is4103.backend.dto.UpdatePartnerRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.service.BusinessPartnerService;
import com.is4103.backend.service.UserService;
import com.is4103.backend.util.errors.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/partner")

public class BusinessPartnerController {

    @Autowired
    private BusinessPartnerService bpService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all")
    public List<BusinessPartner> getAllBusinessPartners() {
        return bpService.getAllBusinessPartners();
    }
    

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all/paginated")
    public Page<BusinessPartner> getBusinessPartnersPage(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return bpService.getBusinessPartnersPage(page, size);
    }

    // @PreAuthorize("hasAnyRole('ADMIN', 'BIZPTNR')")
    @GetMapping(path = "/{id}")
    public BusinessPartner getBusinessPartnerById(@PathVariable Long id) {
        return bpService.getBusinessPartnerById(id);
    }

    @GetMapping(path = "/events/{id}")
    public List<Event> getEventsById(@PathVariable Long id) {
        return bpService.getAllEvents(id);
    }

    @PostMapping(value = "/register")
    public SignupResponse registerNewBusinessPartner(@RequestBody @Valid SignupRequest signupRequest) {
        // return 
        try{
    
            if (userService.emailExists(signupRequest.getEmail())) {
                throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
            }else{
            
           bpService.registerNewBusinessPartner(signupRequest, false);
    
            }
        
            }
            catch(UserAlreadyExistsException userAlrExistException){
               return new SignupResponse("alreadyExisted");
            }
    
           
            return new SignupResponse("success");
    }

    @GetMapping(path = "/followers/{id}")
    public List<Attendee> getFollowers(@PathVariable Long id) {
        return bpService.getFollowersById(id);
    }

    @GetMapping(path = "/following/{id}")
    public List<EventOrganiser> getFollowing(@PathVariable Long id) {
        return bpService.getFollowingById(id);
    }

    @PreAuthorize("hasAnyRole('BIZPTNR')")
    @PostMapping(value ="/followEO")
    public ResponseEntity<BusinessPartner> followEventOrganiser(@RequestBody @Valid FollowRequest followEORequest){
        BusinessPartner user = bpService.getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = bpService.followEventOrganiser(user, followEORequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('BIZPTNR')")
    @PostMapping(value ="/unfollowEO")
    public ResponseEntity<BusinessPartner> unfollowEventOrganiser(@RequestBody @Valid FollowRequest followEORequest){
        BusinessPartner user = bpService.getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = bpService.unfollowEventOrganiser(user, followEORequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/register/noverify")
    public BusinessPartner registerNewBusinessPartnerNoVerify(@RequestBody @Valid SignupRequest signupRequest) {
        return bpService.registerNewBusinessPartner(signupRequest, true);
    }

    @PreAuthorize("hasAnyRole('BIZPTNR')")
    @PostMapping(value ="/update")
    public ResponseEntity<BusinessPartner> updatePartner(@RequestBody @Valid UpdatePartnerRequest updatePartnerRequest) {
        BusinessPartner user = bpService.getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        // verify user id
        if (updatePartnerRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }

        user = bpService.updatePartner(user, updatePartnerRequest);
        return ResponseEntity.ok(user);
    }

    
 




}
